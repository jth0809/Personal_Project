package com.personal.backend.repository;

import com.personal.backend.domain.Cart;
import com.personal.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 사용자를 기준으로 장바구니를 찾는 기능
    Optional<Cart> findByUser(User user);
}
