package org.chatservice.model.dto.response;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String id;
    private String chatId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean read;

}