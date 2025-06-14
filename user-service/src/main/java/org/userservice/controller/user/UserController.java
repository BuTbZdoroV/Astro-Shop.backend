package org.userservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public final class UserController {

    private final UserService userService;

    @GetMapping("/find")
    public ResponseEntity<?> find(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        return userService.find(new UserRequest(id, name, email));
    }

    /**
     * Можно было бы и через @RequestHeader(value = "X-User-Id") реализовать, сделал ради демонстрации
     */
    @GetMapping("/getUserPrincipalData")
    public ResponseEntity<?> getUserPrincipalData(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getUserPrincipalData(userPrincipal);
    }

}
