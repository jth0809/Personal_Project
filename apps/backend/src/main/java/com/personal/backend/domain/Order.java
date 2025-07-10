package com.personal.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // DB 예약어와 충돌을 피하기 위해 'orders' 사용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 사용자의 주문인지 연결 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 하나의 주문은 여러 개의 주문 상품을 가짐 (일대다 관계)
    // mappedBy는 OrderItem의 'order' 필드에 의해 관리됨을 의미
    // cascade = CascadeType.ALL: 주문이 저장될 때 주문 상품도 함께 저장됨
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, LocalDateTime orderDate, OrderStatus status) {
        this.user = user;
        this.orderDate = orderDate;
        this.status = status;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 👇 핵심 수정: 주문 취소 로직을 엔티티 내부에 추가합니다.
    /**
     * 주문을 취소합니다.
     * TODO: 실제로는 상품 재고를 다시 늘리는 등의 로직이 추가되어야 합니다.
     */
    public void cancel() {
        // 이미 배송이 시작된 경우 등 취소가 불가능한 상태에 대한 검증 로직이 필요합니다.
        if (status == OrderStatus.COMPLETED) { // 예시: 완료된 주문은 취소 불가
            throw new IllegalStateException("이미 완료된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELED;
    }
}