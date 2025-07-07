package org.productservice.repository;

import org.productservice.model.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {
    List<Offer> findAllByLotId(Long lotId);

    @Query("SELECT o.name FROM Offer o WHERE o.id = :id")
    String findNameById(@Param("id") Long id);

    Integer countByUserId(Long userId);
    Integer countByUserIdAndActiveTrue(Long userId);
    Long countByActiveTrue();
}
