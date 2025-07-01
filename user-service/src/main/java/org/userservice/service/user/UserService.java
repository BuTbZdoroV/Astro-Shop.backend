package org.userservice.service.user;

import io.swagger.v3.oas.annotations.Operation;
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
import org.userservice.model.dto.request.ProfileRequest;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.dto.response.ProfileResponse;
import org.userservice.model.dto.response.UserResponse;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;
import org.userservice.service.utils.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public final ProfileService profileService;

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

    @Transactional(readOnly = true)
    public ResponseEntity<?> getFullData(UserRequest userRequest) {
        if (userRequest == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        User user = userUtils.findByRequest(userRequest, userRepository);
        UserResponse response = userUtils.buildFullResponse(user);

        logger.info(response.toString());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getBasicData(UserRequest userRequest) {
        if (userRequest == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        User user = userUtils.findByRequest(userRequest, userRepository);
        UserResponse response = userUtils.buildBasicResponse(user);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getTopByBuyerRating(Integer limit) {
        if (limit == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (limit <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        List<User> users = userRepository.findTopByBuyerRating(limit);

        if (users.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        List<UserResponse> userResponses = users.stream().map(user ->
                UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build()).toList();

        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }


    public ResponseEntity<Long> getCountAll() {
        return ResponseEntity.ok(userRepository.count());
    }



}
