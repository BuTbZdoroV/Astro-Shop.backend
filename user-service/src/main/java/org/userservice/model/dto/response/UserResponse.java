package org.userservice.model.dto.response;

import lombok.*;
import org.userservice.model.entity.User;

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
    private String imageUrl;
    private Set<User.Role> roles;
    private User.AuthProvider authProvider;
}
