package org.gatewayservice.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${backend.url}")
    public String backendURL;

    @Bean
    public WebClient authServiceClient() {
        return WebClient.builder()
                .baseUrl(backendURL)
                .build();
    }
}
