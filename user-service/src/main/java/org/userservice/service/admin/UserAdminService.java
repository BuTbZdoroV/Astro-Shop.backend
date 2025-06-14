package org.userservice.service.admin;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.model.entity.User;
import org.userservice.repository.UserRepository;
import org.userservice.service.utils.UserUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAdminService {
    private final Logger logger = LoggerFactory.getLogger(UserAdminService.class);

    private final UserRepository userRepository;
    private final UserUtils userUtils;

    @Transactional
    public ResponseEntity<?> update(UserRequest userRequest) {
        User user = userUtils.findByRequest(userRequest, userRepository);
        Map<String, Object> changedData = new HashMap<>();

        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
            changedData.put("name", userRequest.getName());
        }

        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
            changedData.put("email", userRequest.getEmail());
        }

        if (userRequest.getRole() != null) {
            user.getRoles().addAll(userRequest.getRole());
            changedData.put("roles", userRequest.getRole());
        }

        if (changedData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        userRepository.save(user);
        return new ResponseEntity<>(changedData, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> delete(UserRequest userRequest) {
        User user = userUtils.findByRequest(userRequest, userRepository);
        userRepository.delete(user);

        logger.info("User deleted: {}", user);

        return ResponseEntity.ok("User with id " + user.getId() + " deleted successfully");
    }

}
