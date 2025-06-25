package org.userservice.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.request.ProfileRequest;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.ProfileResponse;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.service.user.ProfileService;
import org.userservice.service.user.UserService;
import org.userservice.service.utils.UserUtils;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public final class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final UserUtils userUtils;

    @GetMapping("/find")
    public ResponseEntity<?> find(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        return userService.find(new UserRequest(id, name, email));
    }

    @Operation(description = "UserData + ProfileData")
    @GetMapping("/getFullData")
    public ResponseEntity<?> getFullData(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        return userService.getFullData(new UserRequest(id, name, email));
    }

    @Operation(description = "Все необходимое для легкого отображения на странице: id, name, profile.imageUrl")
    @GetMapping("/getBasicData")
    public ResponseEntity<?> getBasicData(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        return userService.getBasicData(new UserRequest(id, name, email));
    }

    /**
     * Можно было бы и через @RequestHeader(value = "X-User-Id") реализовать, сделал ради демонстрации
     */
    @GetMapping("/getUserPrincipalData")
    public ResponseEntity<?> getUserPrincipalData(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getUserPrincipalData(userPrincipal);
    }

}
