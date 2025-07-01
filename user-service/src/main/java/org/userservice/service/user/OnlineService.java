package org.userservice.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.response.OnlineResponse;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OnlineService {

    private final Logger logger = LoggerFactory.getLogger(OnlineService.class);
    private final UserRepository userRepository;
    private final Clock clock;

    @Transactional
    public ResponseEntity<Void> ping(Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Instant now = Instant.now(clock);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setLastSeenAt(now);
        user.setIsOnline(isUserOnline(userId));

        logger.info("{} {}", user.getLastSeenAt(), user.getIsOnline());

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> checkStatus(Long userId) {
        boolean isOnline = isUserOnline(userId);
        Instant lastSeenAt = userRepository.findLastSeenAtByUserId(userId)
                .orElse(null);

        return ResponseEntity.ok(new OnlineResponse(isOnline, lastSeenAt));
    }

    @Scheduled(fixedRate = 60000) // Каждую минуту
    @Transactional
    public void updateOfflineStatuses() {
        Instant threshold = Instant.now(clock).minus(1, ChronoUnit.MINUTES);
        logger.info("Updating offline statuses for {}", threshold);
        userRepository.markUsersOffline(threshold);
    }

    // Проверка, онлайн ли пользователь
    private boolean isUserOnline(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getLastSeenAt() == null) return false;
                    Instant oneMinutesAgo = Instant.now(clock).minus(1, ChronoUnit.MINUTES);
                    return user.getLastSeenAt().isAfter(oneMinutesAgo);
                })
                .orElse(false);
    }

}

