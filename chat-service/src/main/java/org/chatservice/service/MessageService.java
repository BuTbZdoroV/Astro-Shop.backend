package org.chatservice.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.chatservice.model.dto.event.ChatUpdateEvent;
import org.chatservice.model.dto.event.MessageReadEvent;
import org.chatservice.model.dto.request.MessageReadsRequest;
import org.chatservice.model.dto.request.MessageRequest;
import org.chatservice.model.dto.response.MessageResponse;
import org.chatservice.model.entity.Chat;
import org.chatservice.model.entity.Message;
import org.chatservice.repository.ChatParticipantRepository;
import org.chatservice.repository.ChatRepository;
import org.chatservice.repository.MessageRepository;
import org.chatservice.service.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MessageUtils messageUtils;

    public Mono<MessageResponse> sendMessage(MessageRequest request) {
        return chatService.findOrCreateChat(request.getSenderId(), request.getRecipientId(), request.getOfferId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found")))
                .flatMap(chat -> {
                    // Создаем новое сообщение
                    Message message = Message.builder()
                            .chatId(chat.getId())
                            .senderId(request.getSenderId())
                            .content(request.getContent())
                            .createdAt(LocalDateTime.now())
                            .build();

                    return messageRepository.save(message)
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to save message")))
                            .flatMap(savedMessage -> {
                                // Обновляем чат
                                chat.setLastMessage(savedMessage.getContent());
                                chat.setLastMessageTime(savedMessage.getCreatedAt());

                                return chatRepository.save(chat)
                                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update chat")))
                                        .flatMap(savedChat -> {
                                            // Строим ответ
                                            MessageResponse response = messageUtils.buildResponse(savedMessage);
                                            response.setRead(false);

                                            // Находим участника чата (получателя) и добавляем сообщение в непрочитанные
                                            Mono<Void> updateUnread = chatParticipantRepository
                                                    .findByChatIdAndUserId(savedChat.getId(), request.getRecipientId())
                                                    .flatMap(participant -> {
                                                        if (participant.getUnreadMessages() == null) {
                                                            participant.setUnreadMessages(new ArrayList<>());
                                                        }
                                                        participant.getUnreadMessages().add(savedMessage.getId());
                                                        return chatParticipantRepository.save(participant);
                                                    }).doOnNext(participant -> logger.info("Updating unread messages for {} for user {}", savedChat.getId(), request.getRecipientId())).then();

                                            // Отправляем сообщение через WebSocket
                                            return updateUnread
                                                    .doOnSuccess(v -> {
                                                        // Отправка получателю
                                                        messagingTemplate.convertAndSendToUser(
                                                                String.valueOf(request.getRecipientId()),
                                                                "/queue/messages",
                                                                response
                                                        );

                                                        // +++ Добавлено: Отправка отправителю +++
                                                        messagingTemplate.convertAndSendToUser(
                                                                String.valueOf(request.getSenderId()),
                                                                "/queue/messages",
                                                                response
                                                        );

                                                        // Широковещательная отправка в чат
                                                        messagingTemplate.convertAndSend(
                                                                "/topic/chat/" + savedChat.getId(),
                                                                response
                                                        );

                                                        sendChatUpdates(savedChat, request.getSenderId(), request.getRecipientId());
                                                    })
                                                    .thenReturn(response);
                                        });
                            });
                });
    }

    public Mono<Void> confirmReadAllMessages(MessageReadsRequest request) {
        return chatRepository.findById(request.getChatId())
                .flatMap(chat -> {
                    if (!chat.getUser1Id().equals(request.getReaderId()) &&
                            !chat.getUser2Id().equals(request.getReaderId())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a chat participant"));
                    }

                    Long partnerId = request.getReaderId().equals(chat.getUser1Id()) ? chat.getUser2Id() : chat.getUser1Id();

                    return chatParticipantRepository.findByChatIdAndUserId(request.getChatId(), request.getReaderId())
                            .flatMap(chatParticipant -> {
                                if (chatParticipant.getUnreadMessages() != null) {
                                    chatParticipant.getUnreadMessages().clear();
                                }

                                return chatParticipantRepository.save(chatParticipant).doOnSuccess(participant -> {
                                    MessageReadEvent event = MessageReadEvent.builder()
                                            .chatId(request.getChatId().toString())
                                            .readerId(request.getReaderId())
                                            .senderId(partnerId)
                                            .build();

                                    // Персональное уведомление отправителю
                                    messagingTemplate.convertAndSend("/queue/message-read", event);

                                    // Широковещательное уведомление в чат
                                    messagingTemplate.convertAndSend(
                                            "/topic/chat/" + request.getChatId().toString() + "/read-status",
                                            event
                                    );
                                    sendChatUpdateForUser(chat, request.getReaderId());
                                });
                            });
                }).then();
    }

    public Mono<Void> confirmReadMessage(MessageReadsRequest request) {
        return messageRepository.findById(request.getMessageId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")))
                .flatMap(message -> chatRepository.findById(message.getChatId())
                        .flatMap(chat -> {
                            // Проверяем доступ пользователя
                            if (!chat.getUser1Id().equals(request.getReaderId()) &&
                                    !chat.getUser2Id().equals(request.getReaderId())) {
                                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a chat participant"));
                            }

                            return chatParticipantRepository.findByChatIdAndUserId(chat.getId(), request.getReaderId())
                                    .flatMap(participant -> {
                                        // Удаляем сообщение из непрочитанных
                                        if (participant.getUnreadMessages() != null) {
                                            participant.getUnreadMessages().remove(request.getMessageId());
                                        }

                                        return chatParticipantRepository.save(participant)
                                                .doOnSuccess(saved -> {
                                                    // Отправляем уведомление отправителю
                                                    MessageReadEvent event = new MessageReadEvent(
                                                            message.getId().toString(),
                                                            message.getChatId().toString(),
                                                            request.getReaderId(),
                                                            message.getSenderId()
                                                    );

                                                    // Персональное уведомление отправителю
                                                    messagingTemplate.convertAndSend(
                                                            "/queue/message-read",
                                                            event
                                                    );

                                                    // Широковещательное уведомление в чат
                                                    messagingTemplate.convertAndSend(
                                                            "/topic/chat/" + message.getChatId() + "/read-status",
                                                            event
                                                    );

                                                    sendChatUpdateForUser(chat, request.getReaderId());

                                                });
                                    });
                        }))
                .then();
    }

    protected void sendChatUpdates(Chat chat, Long user1Id, Long user2Id) {
        sendChatUpdateForUser(chat, user1Id);
        sendChatUpdateForUser(chat, user2Id);
    }

    public void sendChatUpdateForUser(Chat chat, Long userId) {
        chatParticipantRepository.findByChatIdAndUserId(chat.getId(), userId)
                .subscribe(participant -> {
                    int unreadCount = participant.getUnreadMessages() != null ?
                            participant.getUnreadMessages().size() : 0;

                    ChatUpdateEvent event = ChatUpdateEvent.builder()
                            .chatId(chat.getId().toString())
                            .lastMessage(chat.getLastMessage())
                            .lastMessageTime(chat.getLastMessageTime())
                            .unreadCount(unreadCount)
                            .userId(userId)
                            .build();

                    messagingTemplate.convertAndSend(
                            "/queue/chat-updates/" + userId.toString(),
                            event
                    );
                });
    }

    public Flux<MessageResponse> getMessagesHistory(ObjectId chatId, Long userId, Pageable pageable) {
        return chatRepository.findById(chatId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Chat not found")))
                .flatMapMany(chat -> validateUserAccess(chat, userId))
                .flatMap(chat -> messageRepository.findByChatId(chatId, pageable)
                        .flatMap(message -> {
                            Long partnerId = userId.equals(chat.getUser1Id()) ? chat.getUser2Id() : chat.getUser1Id();
                            return chatParticipantRepository.findByChatIdAndUserId(chatId, partnerId)
                                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "chatParticipant NOT FOUND!")))
                                    .map(chatParticipant -> MessageResponse.builder()
                                            .id(message.getId().toString())
                                            .chatId(message.getChatId().toString())
                                            .senderId(message.getSenderId())
                                            .content(message.getContent())
                                            .createdAt(message.getCreatedAt())
                                            .read(!chatParticipant.getUnreadMessages().contains(message.getId()))
                                            .build()).doOnNext(response -> {
                                        logger.info("Getting messages history for {} for user {}, {}", chatId, partnerId, response);
                                    });
                        }));
    }

    private Mono<Chat> validateUserAccess(Chat chat, Long userId) {
        if (!chat.getUser1Id().equals(userId) && !chat.getUser2Id().equals(userId)) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Access denied"));
        }
        return Mono.just(chat);
    }

}