package org.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.chatservice.model.dto.request.MessageReadsRequest;
import org.chatservice.model.dto.request.MessageRequest;
import org.chatservice.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public Mono<?> handleMessage(@Payload MessageRequest message) {
        return messageService.sendMessage(message)
                .doOnNext(response -> log.debug("Message sent: {}", response))
                .doOnError(error -> log.error("Error: {}", error.getMessage()))
                .then();
    }

    @MessageMapping("/message.read")
    public Mono<Void> handleMessageRead(@Payload MessageReadsRequest request) {
        return messageService.confirmReadMessage(request);
    }

    @MessageMapping("/message.allReads")
    public Mono<Void> handleAllReadMessages(@Payload MessageReadsRequest request) {
        return messageService.confirmReadAllMessages(request);
    }

    @GetMapping("/history")
    public Flux<?> history(@RequestParam ObjectId chatId,
                           @PageableDefault(sort = "createdAt", direction = DESC) Pageable pageable,
                           @RequestHeader("X-User-Id") Long userId) {
        return messageService.getMessagesHistory(chatId, userId, pageable);
    }

}
