package com.personal.backend.repository;

import com.personal.backend.domain.Order;
import com.personal.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 사용자의 모든 주문 내역을 찾는 기능을 정의합니다.
    List<Order> findByUser(User user);
}
