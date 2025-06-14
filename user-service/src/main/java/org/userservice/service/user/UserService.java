package org.userservice.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;
import org.userservice.service.utils.UserUtils;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserUtils userUtils;

    /**
     * Поиск пользователя в базе данных если хотя бы один уникальный модификатор не равен null
     *
     * @return UserResponse DTO хранящий данные пользователя
     */
    public ResponseEntity<?> find(UserRequest userRequest) {
        User user = userUtils.findByRequest(userRequest, userRepository);
        UserResponse userResponse = userUtils.buildResponse(user);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getUserPrincipalData(UserPrincipal userPrincipal) {
        if (userPrincipal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() ->
                new AuthenticationServiceException("User with id: " + userPrincipal.getId() + " not found"));

        UserResponse response = userUtils.buildResponse(user);
        logger.info(response.toString());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
