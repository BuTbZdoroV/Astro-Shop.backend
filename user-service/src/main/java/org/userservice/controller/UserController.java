package org.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.authinfo.UserPrincipal;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.service.UserService;

@RestController
@RequestMapping("/users")
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

    @GetMapping("/getUserPrincipalData")
    public ResponseEntity<?> getUserPrincipalData(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.getUserPrincipalData(userPrincipal);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok().build();
    }

}
