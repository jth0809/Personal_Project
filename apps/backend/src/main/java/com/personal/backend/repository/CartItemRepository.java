package com.personal.backend.repository;

import com.personal.backend.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // 특정 장바구니(Cart)에 속한 모든 아이템을 한 번에 삭제하는 기능
    void deleteAllByCart_Id(Long cartId);
}
