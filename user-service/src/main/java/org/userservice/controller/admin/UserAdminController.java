package org.userservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.dto.request.UserRequest;
import org.userservice.service.admin.UserAdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users/admin")
public final class UserAdminController {

    private final UserAdminService userAdminService;

    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequest userRequest) {
        return userAdminService.update(userRequest);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody UserRequest userRequest) {
        return userAdminService.delete(userRequest);
    }

}
