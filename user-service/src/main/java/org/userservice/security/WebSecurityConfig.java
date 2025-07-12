package org.userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.service.utils.JwtUtils;
import org.userservice.service.user.oauth.GoogleOAuthUserService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final GoogleOAuthUserService googleOAuthUserService;
    private final JwtUtils jwtUtils;
    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/favicon.ico", "/css/**", "/js/**", "/ws/**", "/api/auth/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oAuth2Login -> oAuth2Login
                        .userInfoEndpoint(userInfo -> userInfo.userService(googleOAuthUserService))
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            UserPrincipal userPrincipal = (UserPrincipal) oauthToken.getPrincipal();

                            String jwt = jwtUtils.generateToken(userPrincipal);
                            response.setHeader("Authorization", "Bearer " + jwt);

                            String safeFrontendUrl = frontendUrl.endsWith("/")
                                    ? frontendUrl : frontendUrl + "/";
                            response.sendRedirect(safeFrontendUrl + "oauth-callback#token=" +
                                    URLEncoder.encode(jwt, StandardCharsets.UTF_8));                        }))
                .logout(logout -> logout
                        .logoutUrl("/oauth2/logout")
                        .logoutSuccessUrl("/")
                        .addLogoutHandler(new SecurityContextLogoutHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();
    }


}
