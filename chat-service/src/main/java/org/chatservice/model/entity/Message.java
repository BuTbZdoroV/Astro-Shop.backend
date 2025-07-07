package org.chatservice.model.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {

    @Id
    private ObjectId id;
    private ObjectId chatId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;

}
