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

    @Column(name = "follower_id")
    private Long followerId;  // Тот, кто подписался

    @Column(name = "followed_id")
    private Long followedId;  // На кого подписались

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")  // Связывает с полем followerId выше
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followedId")  // Связывает с полем followedId выше
    @JoinColumn(name = "followed_id", insertable = false, updatable = false)
    private User followed;



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