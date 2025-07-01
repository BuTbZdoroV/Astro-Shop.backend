package org.userservice.controller.user.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.userservice.service.user.oauth.JwtService;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public final class JwtController {
    private final JwtService jwtService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        return jwtService.validateToken(token);
    }
}
