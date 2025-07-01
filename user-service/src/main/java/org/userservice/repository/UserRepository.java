package org.userservice.repository;

import org.hibernate.query.spi.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.userservice.model.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);

    @Query("SELECT u.lastSeenAt FROM users u WHERE u.id = :userId")
    Optional<Instant> findLastSeenAtByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE users u SET u.isOnline = false WHERE u.lastSeenAt < :threshold AND u.isOnline = true")
    void markUsersOffline(@Param("threshold") Instant threshold);

    List<User> findAllByIsOnlineTrue();

    @Query("SELECT u FROM users u JOIN u.profile p ORDER BY p.buyerRating DESC LIMIT :limit")
    List<User> findTopByBuyerRating(@Param("limit") Integer limit);

    @Query("SELECT u FROM users u WHERE u.id = :userId")
    Optional<User> findUserInfo(@Param("userId") Long userId);

}

