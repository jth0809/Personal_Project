package com.personal.backend.repository;

import com.personal.backend.domain.Order;
import com.personal.backend.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 사용자의 모든 주문 내역을 찾는 기능을 정의합니다.
    Page<Order> findByUser(User user,Pageable pageable);
    List<Order> findByUser(User user);
    Optional<Order> findByPgOrderId(String pgOrderId);
}
