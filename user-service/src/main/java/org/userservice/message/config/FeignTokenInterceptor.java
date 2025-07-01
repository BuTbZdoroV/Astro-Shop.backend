package org.userservice.message.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Interceptor для автоматической передачи JWT токена в Feign-запросах.
 * <p>
 * Копирует заголовок Authorization из текущего HTTP-запроса во все исходящие
 * Feign-запросы, обеспечивая сквозную передачу аутентификации между микросервисами.
 * </p>
 */
@Component
public class FeignTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                String token = attributes.getRequest()
                        .getHeader("Authorization");

                if (token != null) {
                    template.header("Authorization", token);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to propagate authorization token: " + e.getMessage());
        }
    }
}