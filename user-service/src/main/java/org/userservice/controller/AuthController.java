package org.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;
import org.userservice.service.AuthService;
import org.userservice.service.utils.JwtUtils;
import org.userservice.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public final class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam(required = false) String provider) {
        return authService.login(provider);
    }

}
