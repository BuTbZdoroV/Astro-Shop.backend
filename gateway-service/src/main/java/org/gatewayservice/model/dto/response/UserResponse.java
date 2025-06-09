package org.gatewayservice.model.dto.response;

import lombok.*;

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
    private Set<Role> roles;
    private AuthProvider authProvider;

    public enum AuthProvider {
        anonymous,
        google,
        github,
        facebook
    }

    public enum Role {
        GUEST,
        USER,
        ADMIN
    }
}
