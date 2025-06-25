package org.userservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.userservice.model.entity.utils.FollowerId;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "followers")
public class Follower {

    @EmbeddedId
    private FollowerId id;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private FollowStatus status;

    public enum FollowStatus {
        ACTIVE,
        MUTED
    }
}
