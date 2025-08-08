package com.personal.backend.repository;

import com.personal.backend.domain.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JpaRepository<관리할 엔티티, 엔티티의 ID 타입>를 상속받습니다.
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA의 'Query Method' 기능:
    // 메소드 이름을 규칙에 맞게 짓는 것만으로, 스프링이 알아서
    // "이름에 특정 키워드가 포함된 상품들을 찾아라"는 SQL을 자동으로 생성해 줍니다.
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    List<Product> findByName(String name);
}
