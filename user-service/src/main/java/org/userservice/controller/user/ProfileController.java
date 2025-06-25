package org.userservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.dto.request.ProfileRequest;
import org.userservice.service.user.ProfileService;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam Long userId) {
        return profileService.get(ProfileRequest.builder().userId(userId).build());
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(ProfileRequest request) {
        return profileService.update(request);
    }

}
