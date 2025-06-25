package org.userservice.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.model.entity.User;
import org.userservice.service.utils.JwtUtils;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final JwtUtils jwtUtils;
    private final UserService userService;

    /**
     * Валидирует JWT токен и возвращает данные пользователя
     *
     * @param token JWT токен с префиксом "Bearer "
     * @return ResponseEntity<></> с DTO данными пользователя если токен валиден,
     * UNAUTHORIZED (401) если токен невалиден,
     * NOT_FOUND (404) если пользователь не найден
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> validateToken(String token) {
        try {
            String jwt = token.replace("Bearer ", "");

            logger.info(jwt);
            if (!jwtUtils.isTokenValid(jwt)) {
                logger.warn("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long userId = Long.valueOf(jwtUtils.extractUserId(jwt));
            if (userId == -1) //Анонимный пользователь
                return ResponseEntity.ok(UserResponse.builder()
                        .id(userId)
                        .build());

            ResponseEntity<UserResponse> response = (ResponseEntity<UserResponse>) userService.find(new UserRequest(userId, null, null));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.error("Token is VALID, but User is NOT FOUND");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            logger.info(response.getBody().toString());
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
