package org.userservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.response.OnlineResponse;
import org.userservice.service.user.OnlineService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/online")
@RequiredArgsConstructor
public class OnlineController {

    private final Logger logger = LoggerFactory.getLogger(OnlineController.class);
    private final OnlineService onlineService;

    @PostMapping("/ping")
    public ResponseEntity<Void> ping(@RequestHeader(value = "X-User-Id") Long id) {
        return onlineService.ping(id);
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(@RequestParam Long userId) {
        return onlineService.checkStatus(userId);
    }

}
