package org.productservice.repository;

import org.productservice.model.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    @Query("select distinct p from Product p full join fetch p.lots")
    List<Product> findAllWithLots();

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.lots WHERE p.id = :id")
    Optional<Product> findByIdWithLots(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.lots WHERE p.name = :name")
    Optional<Product> findByNameWithLots(@Param("name") String name);
}
