package org.userservice.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    /**
     * Поиск пользователя в базе данных если хотя бы один уникальный модификатор не равен null
     *
     * @return UserResponse DTO хранящий данные пользователя
     */
    public ResponseEntity<?> find(UserRequest userRequest) {

        if (userRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<User> user = Optional.empty();
        if (userRequest.getId() != null) {
            user = userRepository.findById(userRequest.getId());
        }
        if (userRequest.getName() != null && user.isEmpty()) {
            user = userRepository.findByName(userRequest.getName());
        }
        if (userRequest.getEmail() != null && user.isEmpty()) {
            user = userRepository.findByEmail(userRequest.getEmail());
        }

        if (user.isEmpty()) {
            logger.warn("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User findedUser = user.get();

        UserResponse userResponse = UserResponse.builder()
                .id(findedUser.getId())
                .name(findedUser.getName())
                .email(findedUser.getEmail())
                .imageUrl(findedUser.getImageUrl())
                .authProvider(findedUser.getAuthProvider())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getUserPrincipalData(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() ->
                new AuthenticationServiceException("User not found"));

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .authProvider(user.getAuthProvider())
                .build();

        logger.info(response.toString());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
