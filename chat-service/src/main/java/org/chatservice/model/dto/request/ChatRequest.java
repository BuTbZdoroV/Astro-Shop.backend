package org.chatservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    private ObjectId id;
    private Long user1Id;
    private Long user2Id;
    private Long offerId;
    private LocalDateTime createdAt;

}
