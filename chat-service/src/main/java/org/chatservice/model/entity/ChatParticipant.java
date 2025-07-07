package org.chatservice.model.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection = "chat_participants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipant {
    @Id
    private ObjectId id;
    private ObjectId chatId;
    private Long userId;
    private List<ObjectId> unreadMessages;
}