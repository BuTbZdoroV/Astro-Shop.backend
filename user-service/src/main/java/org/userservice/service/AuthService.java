package org.userservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;
import org.userservice.service.oauth.GoogleOAuthUserService;
import org.userservice.service.utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    /**
     * Обрабатывает вход пользователя в систему
     *
     * @param provider Провайдер аутентификации (например, "google").
     *                 Если null или пустая строка - создает анонимного пользователя
     * @return ResponseEntity с JWT токеном и данными пользователя для анонимного входа,
     *         или редирект на OAuth2 провайдер для авторизованного входа.
     *         @see GoogleOAuthUserService
     */
    @Transactional
    public ResponseEntity<?> login(String provider) {
        if (provider == null || provider.isEmpty()) {
            User anonymous = User.builder()
                    .name("Anonymous_" + UUID.randomUUID())
                    .email("anonymous@gmail.com")
                    .roles(Set.of(User.Role.GUEST))
                    .authProvider(User.AuthProvider.anonymous)
                    .build();

            User savedUser = userRepository.save(anonymous);
            UserPrincipal anonymousPrincipal = UserPrincipal.create(savedUser, new HashMap<>());
            String token = jwtUtils.generateToken(anonymousPrincipal);
            logger.info("Generated JWT token: {}", token);
            logger.info("Created new Anonymous: {}", anonymousPrincipal);
            return ResponseEntity.ok(Map.of("token", token, "userData", anonymousPrincipal));
        } else {
            String redirectUrl = "http://localhost:8080/oauth2/authorization/" + provider;
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        }
    }

}
