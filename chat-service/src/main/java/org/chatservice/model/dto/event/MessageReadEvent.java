package org.chatservice.model.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadEvent {
    private String messageId;
    private String chatId;
    private Long readerId;
    private Long senderId;



}