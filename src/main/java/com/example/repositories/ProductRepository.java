package com.example.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.presentation ORDER BY p.name ASC")
    List<Product> findAllWithPresentation();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.presentation WHERE p.id = :id")
    Optional<Product> findByIdWithPresentation(@Param("id") Long id);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.presentation",
           countQuery = "SELECT count(p) FROM Product p")
    Page<Product> findAllWithPresentationPaged(Pageable pageable);
}
