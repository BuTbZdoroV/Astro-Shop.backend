package org.productservice.repository;

import org.productservice.model.entity.Lot;
import org.productservice.model.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotRepository extends JpaRepository<Lot, Long> {
}
