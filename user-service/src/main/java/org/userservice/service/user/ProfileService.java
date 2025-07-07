package org.userservice.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.dto.request.ProfileRequest;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.ProfileResponse;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.model.entity.Profile;
import org.userservice.model.entity.User;
import org.userservice.repository.ProfileRepository;
import org.userservice.repository.UserRepository;
import org.userservice.service.utils.ProfileUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"profiles"})
public class ProfileService {
    private final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileUtils profileUtils;

    @Transactional(readOnly = true)
    @Cacheable(key = "'user:' + #request.userId", unless = "#result.body == null")
    public ResponseEntity<?> get(ProfileRequest request) {
        if (request.getUserId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is null");

        Profile profile = profileRepository.findByUserId(request.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found with userId: " + request.getUserId()));

        ProfileResponse response = profileUtils.buildResponse(profile, request.getUserId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'user:' + #request.id", value = "profiles")
            },
            evict = {
                    @CacheEvict(key = "'user:' + #request.id + ':full'", value = "userData")
            }
    )
    public ResponseEntity<?> update(UserRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is null");

        User user = userRepository.findById(request.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with userId: " + request.getId()));
        Profile profile = user.getProfile();

        Map<String, Object> changedData = new HashMap<>();

        if (request.getName() != null) {
            changedData.put("name", request.getName());
            user.setName(request.getName());
        }

        if (request.getProfile().getInfo() != null) {
            changedData.put("info", request.getProfile().getInfo());
            profile.setInfo(request.getProfile().getInfo());
        }

        if (request.getProfile().getImageUrl() != null) {
            changedData.put("imageUrl", request.getProfile().getImageUrl());
            profile.setImageUrl(request.getProfile().getImageUrl());
        }

        if (request.getProfile().getBackgroundUrl() != null) {
            changedData.put("backgroundUrl", request.getProfile().getBackgroundUrl());
            profile.setBackgroundUrl(request.getProfile().getBackgroundUrl());
        }

        if (request.getProfile().getCustomSettings() != null) {
            changedData.put("customSettings", request.getProfile().getCustomSettings());
            profile.setCustomSettings(request.getProfile().getCustomSettings());
        }

        if (request.getProfile().getSocialLinks() != null) {
            changedData.put("socialLinks", request.getProfile().getSocialLinks());
            profile.setSocialLinks(request.getProfile().getSocialLinks());
        }

        if (changedData.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        logger.info(changedData.toString());

        User savedUser = userRepository.save(user);
        UserResponse userResponse = profileUtils.buildFullResponse(savedUser);

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }


}
