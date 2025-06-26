package org.userservice.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.dto.request.ProfileRequest;
import org.userservice.model.dto.response.ProfileResponse;
import org.userservice.model.entity.Profile;
import org.userservice.repository.ProfileRepository;
import org.userservice.service.utils.ProfileUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final ProfileUtils profileUtils;

    @Transactional(readOnly = true)
    public ResponseEntity<?> get(ProfileRequest request) {
        if (request.getUserId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is null");

        Profile profile = profileRepository.findByUserId(request.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found with userId: " + request.getUserId()));

        ProfileResponse response = profileUtils.buildResponse(profile, request.getUserId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> update(ProfileRequest request) {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request is null");

        Profile profile = profileRepository.findByUserId(request.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found with userId: " + request.getUserId()));

        Map<String, Object> changedData = new HashMap<>();

        if (request.getBio() != null) {
            changedData.put("bio", profile.getBio());
            profile.setBio(request.getBio());
        }

        if (request.getBuyerRating() != null) {
            changedData.put("buyerRating", profile.getBuyerRating());
            profile.setBuyerRating(request.getBuyerRating());
        }

        if (request.getSellerRating() != null) {
            changedData.put("sellerRating", profile.getSellerRating());
            profile.setSellerRating(request.getSellerRating());
        }

        if (request.getInfo() != null) {
            changedData.put("info", profile.getInfo());
            profile.setInfo(request.getInfo());
        }

        if (request.getImageUrl() != null) {
            changedData.put("imageUrl", profile.getImageUrl());
            profile.setImageUrl(request.getImageUrl());
        }

        if (request.getBannerUrl() != null) {
            changedData.put("bannerUrl", profile.getBackgroundUrl());
            profile.setBackgroundUrl(request.getBannerUrl());
        }

        if (request.getCustomSettings() != null) {
            changedData.put("customSettings", profile.getCustomSettings());
            profile.setCustomSettings(profile.getCustomSettings());
        }

        if (request.getSocialLinks() != null) {
            changedData.put("socialLinks", profile.getSocialLinks());
            profile.setSocialLinks(profile.getSocialLinks());
        }

        if (request.getUnlockedBadges() != null) {
            changedData.put("unlockedBadges", profile.getUnlockedBadges());
            profile.setUnlockedBadges(profile.getUnlockedBadges());
        }

        if (changedData.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        Profile updatedProfile = profileRepository.save(profile);
        ProfileResponse profileResponse = profileUtils.buildResponse(updatedProfile, request.getUserId());

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }


}
