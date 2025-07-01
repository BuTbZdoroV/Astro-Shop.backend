package org.userservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.request.ProfileRequest;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.service.user.ProfileService;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public final class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam Long userId) {
        return profileService.get(ProfileRequest.builder().userId(userId).build());
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || userPrincipal.getId() == -1 || !userPrincipal.getId().equals(request.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userPrincipal is null");
        }
        return profileService.update(request);
    }

}
