package org.userservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

}
