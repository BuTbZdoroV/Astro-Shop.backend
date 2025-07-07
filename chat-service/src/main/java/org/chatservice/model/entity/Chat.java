package org.chatservice.model.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
@CompoundIndex(def = "{'user1Id': 1, 'user2Id': 1, 'offerId': 1}", unique = true)
public class Chat {

    @Id
    private ObjectId id;

    private Long user1Id;

    private Long user2Id;

    private Long offerId;
    private LocalDateTime createdAt;

    private String lastMessage;
    private LocalDateTime lastMessageTime;

}
