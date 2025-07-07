package org.chatservice.model.dto.event;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ChatUpdateEvent {
    private String chatId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
    private Long userId;
}