package org.userservice.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class OnlineConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
