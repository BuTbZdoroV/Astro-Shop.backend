package org.chatservice.model.dto.response;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ChatListResponse {
    private String id;
    private Long partnerId;
    private String partnerName;
    private String partnerAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private List<String> unreadMessages;
    private Long offerId;
    private String offerName;
}
