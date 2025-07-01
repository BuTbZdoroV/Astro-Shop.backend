package org.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.userservice.model.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private Long id;
    private String name;
    private String email;
    private Set<User.Role> role;
    private ProfileRequest profile;

    public UserRequest(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }


}
