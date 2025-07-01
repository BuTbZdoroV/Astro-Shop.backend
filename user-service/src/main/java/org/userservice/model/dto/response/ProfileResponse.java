package org.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    Long id;
    Long userId;
    Double sellerRating;
    Double buyerRating;
    String info;
    String imageUrl;
    String backgroundUrl;
    Map<String, String> socialLinks;
    Set<String> unlockedBadges;
    Map<String, Object> customSettings;

}
