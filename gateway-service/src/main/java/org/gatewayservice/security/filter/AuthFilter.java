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

@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter {
    private final WebClient authServiceClient;
    private final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    /**
     * Фильтрует входящие запросы и проверяет аутентификацию
     *
     * @param exchange Объект ServerWebExchange с данными запроса
     * @param chain Цепочка фильтров Gateway
     * @return Mono<Void> для продолжения или завершения обработки запроса
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (path.startsWith("/api/auth/") || path.startsWith("/api/jwt/") || path.startsWith("/login") || path.startsWith("/oauth2/")) {
            logger.info(path);
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return authServiceClient.post()
                .uri("/api/jwt/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .flatMap(userInfo -> {
                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(builder -> builder
                                    .header("X-User-Id", String.valueOf(userInfo.getId()))
                                    .header("X-User-Email", userInfo.getEmail())
                                    .header("X-User-Roles", String.valueOf(userInfo.getRoles()))
                            )
                            .build();

                    return chain.filter(modifiedExchange);
                })
                .onErrorResume(e -> {
                    logger.error("Token validation error", e);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}