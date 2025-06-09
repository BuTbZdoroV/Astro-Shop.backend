package org.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.service.JwtService;
import org.userservice.service.utils.JwtUtils;
import org.userservice.service.UserService;

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
