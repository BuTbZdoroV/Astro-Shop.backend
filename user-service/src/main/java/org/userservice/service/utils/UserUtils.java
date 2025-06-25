package org.userservice.service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.ProfileResponse;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserUtils {

    public User findByRequest(UserRequest userRequest, UserRepository userRepository) {
        if (userRequest == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userRequest cannot be null");
        if (userRepository == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userRepository cannot be null");

        Optional<User> userOptional = Optional.empty();

        if (userRequest.getId() != null) {
            userOptional = userRepository.findById(userRequest.getId());
        }
        if (userRequest.getName() != null && userOptional.isEmpty()) {
            userOptional = userRepository.findByName(userRequest.getName());
        }
        if (userRequest.getEmail() != null && userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(userRequest.getEmail());
        }

        return userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserResponse buildResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .authProvider(user.getAuthProvider())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }


    public UserResponse buildFullResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .authProvider(user.getAuthProvider())
                .createdAt(user.getCreatedAt())
                .profile(ProfileResponse.builder()
                        .bio(user.getProfile().getBio())
                        .userId(user.getId())
                        .unlockedBadges(user.getProfile().getUnlockedBadges())
                        .themeColorHex(user.getProfile().getThemeColorHex())
                        .socialLinks(user.getProfile().getSocialLinks())
                        .customSettings(user.getProfile().getCustomSettings())
                        .bannerUrl(user.getProfile().getBannerUrl())
                        .imageUrl(user.getProfile().getImageUrl())
                        .id(user.getProfile().getId())
                        .build())
                .build();
    }

}
