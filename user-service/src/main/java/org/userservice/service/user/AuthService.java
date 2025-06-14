package org.userservice.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
                    .id(-1L)
                    .roles(Set.of(User.Role.GUEST))
                    .authProvider(User.AuthProvider.anonymous)
                    .build();

            UserPrincipal anonymousPrincipal = UserPrincipal.create(anonymous, new HashMap<>());
            String token = jwtUtils.generateToken(anonymousPrincipal, 3600000L);
            logger.info("Generated JWT token: {}", token);
            logger.info("Created new Anonymous: {}", anonymousPrincipal);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            String redirectUrl = "http://localhost:8080/oauth2/authorization/" + provider;
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                logger.warn("No Authorization header found");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendRedirect("/");
                return;
            }
            token = token.replace("Bearer ", "");
            if (!jwtUtils.isTokenValid(token)) {
                logger.warn("Invalid JWT token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendRedirect("/");
                return;
            }
            logger.info("Logout request received: {}", token);
            new SecurityContextLogoutHandler().logout(request, response, null);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
