package org.userservice.model.authinfo;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.userservice.model.entity.User;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserPrincipal implements OAuth2User {

    private Long id;
    private String name;
    private String email;
    private String imageUrl;
    private Map<String, Object> attributes;

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        return UserPrincipal.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .attributes(attributes)
                .build();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
