package org.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.userservice.model.entity.Profile;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    Long id;
    Long userId;
    Double sellerRating;
    Double buyerRating;
    String bio;
    String info;
    String imageUrl;
    String bannerUrl;
    String themeColorHex;
    Map<String, String> socialLinks;
    Set<String> unlockedBadges;
    Map<String, Object> customSettings;

}
