package org.userservice.model.entity.utils;

import jakarta.persistence.*;
import lombok.*;
import org.userservice.model.entity.User;

import java.util.Objects;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FollowerId {

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Column(name = "followed_id", nullable = false)
    private Long followedId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowerId that = (FollowerId) o;
        return Objects.equals(followerId, that.followerId) &&
                Objects.equals(followedId, that.followedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }
}