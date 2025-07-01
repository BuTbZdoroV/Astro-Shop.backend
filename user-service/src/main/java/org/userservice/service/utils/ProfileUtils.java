package org.userservice.service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.userservice.model.dto.response.ProfileResponse;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.model.entity.Profile;
import org.userservice.model.entity.User;

@Component
@RequiredArgsConstructor
public class ProfileUtils {

    public ProfileResponse buildResponse(final Profile profile, long userId) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(userId)
                .info(profile.getInfo())
                .backgroundUrl(profile.getBackgroundUrl())
                .customSettings(profile.getCustomSettings())
                .socialLinks(profile.getSocialLinks())
                .unlockedBadges(profile.getUnlockedBadges())
                .imageUrl(profile.getImageUrl())
                .build();
    }

    public UserResponse buildFullResponse(final User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profile(ProfileResponse.builder()
                        .id(user.getProfile().getId())
                        .info(user.getProfile().getInfo())
                        .imageUrl(user.getProfile().getImageUrl())
                        .backgroundUrl(user.getProfile().getBackgroundUrl())
                        .customSettings(user.getProfile().getCustomSettings())
                        .socialLinks(user.getProfile().getSocialLinks())
                        .unlockedBadges(user.getProfile().getUnlockedBadges())
                        .userId(user.getId())
                        .buyerRating(user.getProfile().getBuyerRating())
                        .sellerRating(user.getProfile().getSellerRating())
                        .build())
                .build();
    }

}
