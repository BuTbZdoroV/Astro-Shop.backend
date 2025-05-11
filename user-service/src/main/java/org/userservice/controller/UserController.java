package org.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.service.UserService;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public final class UserController {

    private final UserService userService;

    @GetMapping("/findUser")
    public ResponseEntity<?> find(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        return userService.find(new UserRequest(id, name, email));
    }

}
