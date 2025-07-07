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
public class MessageRequest {

    private ObjectId id;
    private ObjectId chatId;
    private Long offerId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean read;

}
