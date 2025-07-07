package org.gatewayservice.security.filter;

import lombok.RequiredArgsConstructor;
import org.gatewayservice.model.dto.response.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReactiveRequestFilter implements GlobalFilter {
    private final WebClient authServiceClient;
    private final Logger logger = LoggerFactory.getLogger(ReactiveRequestFilter.class);
    private static final Long ANONYMOUS_USER_ID = -1L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        logger.info(path);
        if (isPublicEndpoint(path)) {
            logger.info("Public endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            logger.warn("Authorization header missing or invalid token: {}", path);
            return respondWithError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }

        return authServiceClient.post()
                .uri("/api/jwt/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .flatMap(userInfo -> processUserInfo(exchange, chain, userInfo))
                .onErrorResume(e -> handleValidationError(exchange, e));
    }

    private Mono<Void> processUserInfo(ServerWebExchange exchange,
                                       GatewayFilterChain chain,
                                       UserResponse userInfo) {
        ServerWebExchange modifiedExchange = addUserHeaders(exchange, userInfo);

        if (isAnonymousUser(userInfo)) {
            return chain.filter(modifiedExchange);
        }

        if (isAdminEndpoint(exchange.getRequest().getPath().toString()) &&
                !hasAdminRole(userInfo.getRoles())) {
            return respondWithError(exchange, HttpStatus.FORBIDDEN, "Admin role required");
        }

        return chain.filter(modifiedExchange);
    }

    private ServerWebExchange addUserHeaders(ServerWebExchange exchange, UserResponse userInfo) {
        return exchange.mutate()
                .request(builder -> {
                    builder.header("X-User-Id", userInfo.getId().toString());
                    if (!isAnonymousUser(userInfo)) {
                        builder
                                .header("X-User-Email", userInfo.getEmail())
                                .header("X-User-Roles", userInfo.getRoles().toString());
                    }
                })
                .build();
    }

    private Mono<Void> handleValidationError(ServerWebExchange exchange, Throwable e) {
        logger.error("Token validation failed: {}", e.getMessage());
        logger.debug("Full error: ", e); // Детальный лог для разработки

        // Добавьте информацию о запросе
        logger.error("Failed request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath());

        return respondWithError(exchange, HttpStatus.UNAUTHORIZED, "Token validation failed");
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/jwt/")
                || path.startsWith("/login")
                || path.startsWith("/oauth2/")
                || path.startsWith("/api/media")
                || path.startsWith("/chat-ws/");
    }

    private boolean isAdminEndpoint(String path) {
        return path.contains("/admin/");
    }

    private boolean hasAdminRole(Set<UserResponse.Role> roles) {
        return roles != null && roles.contains(UserResponse.Role.ADMIN);
    }

    private boolean isAnonymousUser(UserResponse userInfo) {
        return userInfo.getId() != null && userInfo.getId().equals(ANONYMOUS_USER_ID);
    }

    private Mono<Void> respondWithError(ServerWebExchange exchange,
                                        HttpStatus status,
                                        String message) {
        logger.warn("{}: {}", status.name(), message);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}