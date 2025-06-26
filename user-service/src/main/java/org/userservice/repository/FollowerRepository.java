package org.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.userservice.model.entity.Follower;
import org.userservice.model.entity.utils.FollowerId;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {

    // ========== Базовые запросы ==========

    // Проверка существования подписки по ID ключа
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    // Поиск по ID ключа
    Optional<Follower> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    // ========== Подписчики и подписки ==========

    // Список подписчиков пользователя
    @Query("SELECT f FROM Follower f " +
            "JOIN FETCH f.follower " +  // Используем связь follower
            "WHERE f.id.followedId = :userId " +
            "ORDER BY f.createdAt DESC")
    List<Follower> findFollowersByFollowedId(@Param("userId") Long userId);

    // Список тех, на кого подписан пользователь
    @Query("SELECT f FROM Follower f " +
            "JOIN FETCH f.followed " +  // Используем связь followed
            "WHERE f.id.followerId = :userId " +
            "ORDER BY f.createdAt DESC")
    List<Follower> findFollowingByUserId(@Param("userId") Long userId);

    // ========== Статистика ==========

    // Количество подписчиков
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.id.followedId = :userId")
    Integer countByFollowedId(@Param("userId") Long userId);

    // Количество подписок пользователя
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.id.followerId = :userId")
    Integer countByFollowerId(@Param("userId") Long userId);

    // ========== Дополнительные методы ==========

    // Поиск по статусу подписки
    @Query("SELECT f FROM Follower f " +
            "WHERE f.id.followerId = :userId AND f.status = :status")
    List<Follower> findByFollowerIdAndStatus(@Param("userId") Long userId,
                                             @Param("status") Follower.FollowStatus status);

}