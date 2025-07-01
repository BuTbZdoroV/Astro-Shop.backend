package org.userservice.service.user.oauth;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.userservice.model.authinfo.OAuthUserInfo;
import org.userservice.model.authinfo.OAuthUserInfoFactory;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.entity.Profile;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoogleOAuthUserService extends DefaultOAuth2UserService {
    private final Logger logger = LoggerFactory.getLogger(GoogleOAuthUserService.class);

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            return processUser(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationException ex) {
            logger.error("OAuth2 authentication failed: {}", ex.getMessage());
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        } catch (Exception ex) {
            logger.error("Unexpected error during OAuth2 processing", ex);
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }
    }

    /**
     * Обработка данных пользователя из OAuth2 провайдера
     * - Регистрирует нового пользователя, если email ну существует в базе данных
     * - Обновляет данные существующего пользователя при совпадении провайдера
     * - Блокирует вход при конфликте провайдеров и прочих ошибок
     *
     * @param userRequest запрос OAuth2 с метаданными клиента.
     * @param oAuth2User  данные пользователя от OAuth2 провайдера
     * @return UserPrincipal с данными пользователя для Spring Security
     * @throws OAuth2AuthenticationException при ошибках валидации или прочих конфликтах
     */
    private OAuth2User processUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws OAuth2AuthenticationException {
        OAuthUserInfo userInfo = OAuthUserInfoFactory.create(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        if (!StringUtils.hasText(userInfo.getEmail())) {
            logger.error("User email is empty");
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
        }

        Optional<User> user = userRepository.findByEmail(userInfo.getEmail());

        User existingUser;
        if (user.isPresent()) {
            existingUser = user.get();
            if (existingUser.getProfile() == null) {
                // Если профиля нет (старые пользователи), создаем дефолтный
                existingUser.setProfile(createDefaultProfile(existingUser, userInfo));
            }

            if (!existingUser.getAuthProvider().equals(User.AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
                logger.error("User with email '{}' already exists.", existingUser.getEmail());
                throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
            }

            existingUser = updateExistingUser(user.get(), userInfo);
        } else {
            existingUser = registerNewUser(userRequest, userInfo);
        }

        return UserPrincipal.create(existingUser, oAuth2User.getAttributes());
    }

    private User updateExistingUser(User user, OAuthUserInfo userInfo) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(userInfo.getName());
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail(userInfo.getEmail());
        }
        return userRepository.save(user);
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuthUserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .authProvider(User.AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))
                .roles(Set.of(User.Role.USER))
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        Profile defaultProfile = createDefaultProfile(savedUser, userInfo);
        savedUser.setProfile(defaultProfile);

        logger.info("New user registered with default profile: {}", savedUser.getEmail());
        return userRepository.save(savedUser);
    }


    private Profile createDefaultProfile(User user, OAuthUserInfo userInfo) {
        return Profile.builder()
                .user(user)
                .sellerRating(0D)
                .buyerRating(0D)
                .info("")
                .backgroundUrl(null) // Дефолтный баннер можно задать позже
                .imageUrl(userInfo.getImageUrl())
                .socialLinks(Map.of()) // Пустые соцсети
                .unlockedBadges(Set.of("welcome")) // Бейдж "новый пользователь"
                .customSettings(Map.of(
                        "layout", "default"
                ))
                .build();
    }

    @Override
    public void setAttributesConverter(Converter<OAuth2UserRequest, Converter<Map<String, Object>, Map<String, Object>>> attributesConverter) {
        super.setAttributesConverter(attributesConverter);
    }
}
