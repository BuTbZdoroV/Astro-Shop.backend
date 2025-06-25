package org.userservice.model.dto.response;

import lombok.*;
import org.userservice.model.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Set<User.Role> roles;
    private User.AuthProvider authProvider;
    private LocalDateTime createdAt;

    private ProfileResponse profile;
}
