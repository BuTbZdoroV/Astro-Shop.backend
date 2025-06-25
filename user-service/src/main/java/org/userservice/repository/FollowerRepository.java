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

    // Проверка существования подписки (с использованием ID объектов)
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Follower f " +
            "WHERE f.id.follower.id = :followerId AND f.id.followed.id = :followedId")
    Boolean existsByFollowerIdAndFollowedId(@Param("followerId") Long followerId,
                                            @Param("followedId") Long followedId);

    // Получение конкретной подписки
    @Query("SELECT f FROM Follower f " +
            "WHERE f.id.follower.id = :followerId AND f.id.followed.id = :followedId")
    Optional<Follower> findByFollowerIdAndFollowedId(@Param("followerId") Long followerId,
                                                     @Param("followedId") Long followedId);

    // ========== Подписчики и подписки ==========

    // Список подписчиков пользователя (с пагинацией)
    @Query("SELECT f FROM Follower f " +
            "JOIN FETCH f.id.follower " +  // JOIN FETCH для избежания N+1
            "WHERE f.id.followed.id = :userId " +
            "ORDER BY f.createdAt DESC")
    List<Follower> findFollowersByUserId(@Param("userId") Long userId);

    // Список тех, на кого подписан пользователь (с пагинацией)
    @Query("SELECT f FROM Follower f " +
            "JOIN FETCH f.id.followed " +  // JOIN FETCH для избежания N+1
            "WHERE f.id.follower.id = :userId " +
            "ORDER BY f.createdAt DESC")
    List<Follower> findFollowingByUserId(@Param("userId") Long userId);

    // ========== Статистика ==========

    // Количество подписчиков
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.id.followed.id = :userId")
    Long countFollowers(@Param("userId") Long userId);

    // Количество подписок пользователя
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.id.follower.id = :userId")
    Long countFollowing(@Param("userId") Long userId);

    // ========== Дополнительные методы ==========

    // Поиск по статусу подписки
    @Query("SELECT f FROM Follower f " +
            "WHERE f.id.follower.id = :userId AND f.status = :status")
    List<Follower> findByFollowerIdAndStatus(@Param("userId") Long userId,
                                             @Param("status") Follower.FollowStatus status);

}