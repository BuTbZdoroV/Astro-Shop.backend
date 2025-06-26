package org.userservice.service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.userservice.model.dto.response.ProfileResponse;
import org.userservice.model.entity.Profile;

@Component
@RequiredArgsConstructor
public class ProfileUtils {

    public ProfileResponse buildResponse(final Profile profile, long userId) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(userId)
                .bio(profile.getBio())
                .bannerUrl(profile.getBackgroundUrl())
                .customSettings(profile.getCustomSettings())
                .socialLinks(profile.getSocialLinks())
                .themeColorHex(profile.getThemeColorHex())
                .unlockedBadges(profile.getUnlockedBadges())
                .imageUrl(profile.getImageUrl())
                .build();
    }

}
