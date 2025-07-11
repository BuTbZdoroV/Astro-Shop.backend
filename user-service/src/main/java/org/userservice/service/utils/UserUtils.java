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
        if (userRequest == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userRequest cannot be null");
        if (userRepository == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userRepository cannot be null");

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
                        .userId(user.getId())
                        .info(user.getProfile().getInfo())
                        .unlockedBadges(user.getProfile().getUnlockedBadges())
                        .socialLinks(user.getProfile().getSocialLinks())
                        .customSettings(user.getProfile().getCustomSettings())
                        .backgroundUrl(user.getProfile().getBackgroundUrl())
                        .imageUrl(user.getProfile().getImageUrl())
                        .id(user.getProfile().getId())
                        .build())
                .build();
    }

    public UserResponse buildBasicResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .profile(ProfileResponse.builder()
                        .imageUrl(user.getProfile().getImageUrl())
                        .build())
                .build();
    }

}
