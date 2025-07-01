package org.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.userservice.model.entity.FavoriteOffer;

import java.util.List;
import java.util.Optional;

public interface FavoriteOfferRepository extends JpaRepository<FavoriteOffer, Long> {
    Boolean existsByUserIdAndOfferId(Long userId, Long offerId);
    Optional<FavoriteOffer> findByUserIdAndOfferId(Long userId, Long offerId);
    List<FavoriteOffer> findAllByUserId(Long userId);
    void deleteByUserIdAndOfferId(Long userId, Long offerId);
    void deleteAllByUserId(Long userId);
    void deleteAllByOfferId(Long offerId);

    Integer countByOfferId(Long offerId);
    Integer countByUserId(Long userId);

    Page<FavoriteOffer> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT fo.offerId, COUNT(fo.offerId) as likeCount " +
            "FROM FavoriteOffer fo " +
            "GROUP BY fo.offerId " +
            "ORDER BY likeCount DESC")
    List<Object[]> findTopOffersByLikes(Pageable pageable);
}
