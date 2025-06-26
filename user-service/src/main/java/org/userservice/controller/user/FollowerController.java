package org.userservice.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.userservice.model.dto.request.FollowerRequest;
import org.userservice.service.user.FollowerService;

@RestController
@RequestMapping("/api/followers")
@RequiredArgsConstructor
@Tag(name = "Followers API", description = "Управление подписками пользователей")
public class FollowerController {

    private final FollowerService followerService;

    @PostMapping("/follow")
    @Operation(summary = "Подписаться на пользователя",
            description = "Создает подписку текущего пользователя (followerId) на другого (followedId)")
    public ResponseEntity<?> follow(@RequestBody FollowerRequest request) {
        return followerService.follow(request);
    }

    @DeleteMapping("/unfollow")
    @Operation(summary = "Отписаться от пользователя",
            description = "Удаляет подписку текущего пользователя (followerId) на другого (followedId)")
    public ResponseEntity<?> unfollow(@RequestBody FollowerRequest request) {
        return followerService.unfollow(request);
    }

    @GetMapping("/checkFollow")
    @Operation(summary = "Проверить подписку",
            description = "Проверяет, подписан ли пользователь (followerId) на другого (followedId)")
    public ResponseEntity<Boolean> checkFollow(@RequestParam Long followerId,
                                               @RequestParam Long followedId) {
        return followerService.checkFollow(FollowerRequest.builder().followerId(followerId).followedId(followedId).build());
    }

    @GetMapping("/getFollowers")
    @Operation(summary = "Получить подписчиков",
            description = "Возвращает список пользователей, подписанных на указанного (followedId)")
    public ResponseEntity<?> getFollowers(@RequestParam Long followedId) {
        return followerService.getFollowers(FollowerRequest.builder().followedId(followedId).build());
    }

    @GetMapping("/getFollowing")
    @Operation(summary = "Получить подписки",
            description = "Возвращает список пользователей, на которых подписан текущий (followerId)")
    public ResponseEntity<?> getFollowing(@RequestParam Long followerId) {
        return followerService.getFollowing(FollowerRequest.builder().followerId(followerId).build());
    }

    @GetMapping("/countFollowers")
    public ResponseEntity<Integer> countFollowers(@RequestParam Long followedId) {
        return followerService.countFollowers(followedId);
    }

    @GetMapping("/countFollowing")
    public ResponseEntity<Integer> countFollowing(@RequestParam Long followerId) {
        return followerService.countFollowing(followerId);
    }
}
