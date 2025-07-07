package org.chatservice.service.utils;

import org.chatservice.model.dto.response.ChatResponse;
import org.chatservice.model.entity.Chat;
import org.springframework.stereotype.Component;

@Component
public class ChatUtils {

    public ChatResponse convertToResponse(Chat chat) {
        return ChatResponse.builder()
                .id(chat.getId().toString())
                .user1Id(chat.getUser1Id())
                .user2Id(chat.getUser2Id())
                .offerId(chat.getOfferId())
                .createdAt(chat.getCreatedAt())
                .build();
    }


}
