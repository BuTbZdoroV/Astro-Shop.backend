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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", insertable = false, updatable = false)
    private User followed;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private FollowStatus status;

    public enum FollowStatus {
        ACTIVE,
        MUTED
    }
}
