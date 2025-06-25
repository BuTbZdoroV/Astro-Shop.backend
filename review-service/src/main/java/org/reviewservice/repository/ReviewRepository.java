package org.reviewservice.repository;

import org.reviewservice.model.dto.other.ReviewStats;
import org.reviewservice.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllBySellerId(Long sellerId);

    @Query("SELECT AVG(r.rating) as avgRating, COUNT(r.id) as reviewsCount " +
            "FROM Review r WHERE r.sellerId = :sellerId")
    Optional<ReviewStats> getSellerRatingStats(@Param("sellerId") Long sellerId);
}
