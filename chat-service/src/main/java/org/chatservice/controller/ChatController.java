package org.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.chatservice.model.dto.request.ChatRequest;
import org.chatservice.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    @PostMapping("/findOrCreate")
    public Mono<?> findOrCreate(@RequestBody ChatRequest request) {
        return chatService.findOrCreate(request);
    }

    @GetMapping("/getChatById/{id}")
    public Mono<?> getChatById(@PathVariable ObjectId id) {
        return chatService.getChatById(id);
    }

    @GetMapping("/getUnreadCountByUserId")
    public Mono<Integer> getUnreadCountByUserId(ObjectId chatId, Long userId) {
        return chatService.getUnreadCountByUserId(chatId, userId)
                .doOnNext(integer ->
                        logger.info("getUnreadCountByUserId - chatId {}, userId {} = unreadCount {}",
                                chatId, userId, integer.toString()));
    }

    @GetMapping("/getAllByUserId")
    public Flux<?> getAllByUserId(
            @RequestParam Long userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return chatService.getAllByUserId(userId, token);
    }

    @SubscribeMapping("/user/queue/chat-updates")
    public void handleChatUpdates() {}

}
