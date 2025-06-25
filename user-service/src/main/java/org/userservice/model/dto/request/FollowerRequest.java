package org.userservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.userservice.model.entity.Follower;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerRequest {

    Long followedId;
    Long followerId;
    Follower.FollowStatus followStatus;

}
