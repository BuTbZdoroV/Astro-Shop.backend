package org.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.service.UserService;

import java.io.IOException;

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

    @GetMapping("/getUserPrincipalData")
    public ResponseEntity<?> getUserPrincipalData(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Authentication authentication) {


        System.out.println("Authentication: " + authentication);
        System.out.println("Principal class: " +
                (authentication != null ? authentication.getPrincipal().getClass() : "null"));

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.getUserPrincipalData(userPrincipal);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.status(HttpStatus.OK).body("Тест успешен");
    }

}
