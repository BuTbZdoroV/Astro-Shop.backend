package org.userservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.userservice.model.entity.Follower;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerResponse {

    Long followedId;
    Long followerId;
    LocalDateTime createdAt;
    Follower.FollowStatus followStatus;

    Boolean followed;

}
