package com.personal.backend.repository;

import com.personal.backend.domain.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {

    Optional<ProductLike> findByUser_IdAndProduct_Id(Long userId, Long productId);

    Page<ProductLike> findByUser_Id(Long userId, Pageable pageable);

    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);

    @Query("SELECT pl.product.id FROM ProductLike pl WHERE pl.user.id = :userId")
    Set<Long> findLikedProductIdsByUserId(@Param("userId") Long userId);
}