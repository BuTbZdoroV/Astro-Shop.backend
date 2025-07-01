package org.userservice.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public FeignTokenInterceptor feignTokenInterceptor() {
        return new FeignTokenInterceptor();
    }
}