package org.userservice.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.userservice.model.dto.request.FollowerRequest;
import org.userservice.model.dto.response.FollowerResponse;
import org.userservice.model.entity.Follower;
import org.userservice.model.entity.utils.FollowerId;
import org.userservice.repository.FollowerRepository;
import org.userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> follow(FollowerRequest request) {

        if (Objects.equals(request.getFollowerId(), request.getFollowedId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "FollowerId and FollowedId cannot be the same");
        }

        if (!userRepository.existsById(request.getFollowerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FollowerId not found");
        }
        if (!userRepository.existsById(request.getFollowedId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FollowedId not found");
        }

        Follower follower = Follower.builder()
                .id(FollowerId.builder()
                        .followerId(request.getFollowerId())
                        .followedId(request.getFollowedId())
                        .build())
                .status(Follower.FollowStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Follower savedFollower = followerRepository.save(follower);

        FollowerResponse followerResponse = FollowerResponse.builder()
                .followerId(savedFollower.getId().getFollowerId())
                .followedId(savedFollower.getId().getFollowedId())
                .followed(true)
                .followStatus(savedFollower.getStatus())
                .createdAt(savedFollower.getCreatedAt())
                .build();

        return ResponseEntity.ok(followerResponse);
    }

    @Transactional
    public ResponseEntity<?> unfollow(FollowerRequest request) {

        Optional<Follower> follower = followerRepository.findByFollowerIdAndFollowedId(request.getFollowerId(), request.getFollowedId());

        if (follower.isPresent()) {
            followerRepository.delete(follower.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Boolean> checkFollow(FollowerRequest request) {
        return new ResponseEntity<>(followerRepository.existsByFollowerIdAndFollowedId(request.getFollowerId(), request.getFollowedId()), HttpStatus.OK);
    }

    /**
     * Получение подписчиков по id пользователя
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getFollowers(FollowerRequest request) {
        List<Follower> followers = followerRepository.findFollowersByFollowedId(request.getFollowedId());
        List<FollowerResponse> response = followers.stream().map(follower -> FollowerResponse.builder()
                .followerId(follower.getId().getFollowerId())
                .followedId(follower.getId().getFollowedId())
                .followed(true)
                .followStatus(follower.getStatus())
                .createdAt(follower.getCreatedAt())
                .build()).toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Получение тех на кого подписан пользователь
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getFollowing(FollowerRequest request) {
        List<Follower> following = followerRepository.findFollowingByUserId(request.getFollowerId());

        List<FollowerResponse> response = following.stream().map(follower -> FollowerResponse.builder()
                .followerId(follower.getId().getFollowerId())
                .followedId(follower.getId().getFollowedId())
                .followed(true)
                .followStatus(follower.getStatus())
                .createdAt(follower.getCreatedAt())
                .build()).toList();

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Integer> countFollowers(Long followedId) {
        return new ResponseEntity<>(followerRepository.countByFollowedId(followedId), HttpStatus.OK);
    }

    public ResponseEntity<Integer> countFollowing(Long followerId) {
        return new ResponseEntity<>(followerRepository.countByFollowerId(followerId), HttpStatus.OK);
    }

}
