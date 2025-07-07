package org.chatservice.model.dto.response;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String id;
    private Long user1Id;
    private Long user2Id;
    private Long offerId;
    private LocalDateTime createdAt;

    private MessageResponse lastMessage;

}