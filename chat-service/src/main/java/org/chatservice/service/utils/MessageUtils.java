package org.chatservice.service.utils;

import org.chatservice.model.dto.response.MessageResponse;
import org.chatservice.model.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    public MessageResponse buildResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId().toString())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .chatId(message.getChatId().toString())
                .senderId(message.getSenderId())
                .build();
    }

}
