package org.chatservice.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.chatservice.model.dto.request.ChatRequest;
import org.chatservice.model.dto.response.ChatListResponse;
import org.chatservice.model.dto.response.ChatResponse;
import org.chatservice.model.entity.Chat;
import org.chatservice.model.entity.ChatParticipant;
import org.chatservice.model.entity.Message;
import org.chatservice.repository.ChatParticipantRepository;
import org.chatservice.repository.ChatRepository;
import org.chatservice.repository.MessageRepository;
import org.chatservice.service.utils.ChatUtils;
import org.chatservice.service.webclient.dto.OfferResponse;
import org.chatservice.service.webclient.dto.ProfileResponse;
import org.chatservice.service.webclient.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatUtils chatUtils;
    private final MessageRepository messageRepository;
    private final WebClient webClient;
    private final ChatParticipantRepository chatParticipantRepository;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Transactional
    public Mono<ChatResponse> findOrCreate(ChatRequest request) {
        return findOrCreateChat(request.getUser1Id(), request.getUser2Id(), request.getOfferId())
                .map(chatUtils::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Mono<ChatResponse> getChatById(ObjectId id) {
        return chatRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(chatUtils::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Mono<Integer> getUnreadCountByUserId(ObjectId chatId, Long userId) {
        return chatParticipantRepository.findByChatIdAndUserId(chatId, userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(chatParticipant -> chatParticipant.getUnreadMessages().size());
    }

    @Transactional
    public Mono<Chat> findOrCreateChat(Long user1Id, Long user2Id, Long offerId) {
        if (user1Id == null || user2Id == null || offerId == null) {
            return Mono.error(new IllegalArgumentException("User IDs and offer ID cannot be null"));
        }

        Long minUserId = Math.min(user1Id, user2Id);
        Long maxUserId = Math.max(user1Id, user2Id);

        return chatRepository.findByUser1IdAndUser2IdAndOfferId(minUserId, maxUserId, offerId)
                .switchIfEmpty(Mono.defer(() -> {
                            Chat newChat = Chat.builder()
                                    .user1Id(minUserId)
                                    .user2Id(maxUserId)
                                    .offerId(offerId)
                                    .createdAt(LocalDateTime.now())
                                    .build();

                            return chatRepository.save(newChat).flatMap(savedChat -> {
                                Mono<ChatParticipant> saveParticipant1 = chatParticipantRepository.save(
                                        ChatParticipant.builder()
                                                .userId(user1Id)
                                                .chatId(savedChat.getId())
                                                .unreadMessages(new ArrayList<>())
                                                .build()
                                );

                                Mono<ChatParticipant> saveParticipant2 = chatParticipantRepository.save(
                                        ChatParticipant.builder()
                                                .userId(user2Id)
                                                .chatId(savedChat.getId())
                                                .unreadMessages(new ArrayList<>())
                                                .build()
                                );

                                return Mono.zip(saveParticipant1, saveParticipant2)
                                        .thenReturn(savedChat);
                            });
                        })
                );
    }

    @Transactional(readOnly = true)
    public Flux<ChatListResponse> getAllByUserId(Long userId, String token) {
        return chatRepository.findAllByUserId(userId).flatMap(chat -> {
                    Long partnerId = userId.equals(chat.getUser1Id()) ? chat.getUser2Id() : chat.getUser1Id();

                    Mono<OfferResponse> offerResponseMono = getOfferInfo(chat.getOfferId(), token);
                    Mono<UserResponse> userResponseMono = getPartnerInfo(partnerId, token);
                    Mono<ChatParticipant> chatParticipantMono = chatParticipantRepository
                            .findByChatIdAndUserId(chat.getId(), userId)
                            .switchIfEmpty(Mono.error(new RuntimeException("Participant not found")));

                    return Mono.zip(offerResponseMono, userResponseMono, chatParticipantMono)
                            .map(objects -> {
                                OfferResponse offerResponse = objects.getT1();
                                UserResponse partnerResponse = objects.getT2();
                                ChatParticipant chatParticipant = objects.getT3();

                                return ChatListResponse.builder()
                                        .id(chat.getId().toString())
                                        .partnerId(partnerId)
                                        .partnerAvatar(partnerResponse.getProfile().getImageUrl())
                                        .partnerName(partnerResponse.getName())
                                        .offerId(offerResponse.getId())
                                        .offerName(offerResponse.getName())
                                        .unreadMessages(chatParticipant.getUnreadMessages() != null ?
                                                chatParticipant.getUnreadMessages().stream().map(ObjectId::toString).collect(Collectors.toList())
                                                : new ArrayList<>())
                                        .lastMessageTime(chat.getLastMessageTime())
                                        .lastMessage(chat.getLastMessage())
                                        .build();
                            });
                })
                .onErrorResume(e -> {
                    logger.error("Error fetching chats for user {}", userId, e);
                    return Flux.error(e);
                });
    }


    private Mono<UserResponse> getPartnerInfo(Long partnerId, String token) {
        return webClient.get()
                .uri("/api/users/getBasicData?id={id}", partnerId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new ServiceUnavailableException("User service unavailable")))
                .bodyToMono(UserResponse.class)
                .onErrorResume(e -> Mono.just(UserResponse.builder()
                        .id(partnerId)
                        .name("Unknown User")
                        .profile(new ProfileResponse(null))
                        .build()));
    }

    private Mono<OfferResponse> getOfferInfo(Long offerId, String token) {
        return webClient.get()
                .uri("/api/offers/get?offerId={offerId}", offerId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new ServiceUnavailableException("Offer service unavailable")))
                .bodyToMono(OfferResponse.class)
                .onErrorResume(e -> Mono.just(OfferResponse.builder()
                        .id(offerId)
                        .name("Unknown Offer")
                        .build()));
    }

}