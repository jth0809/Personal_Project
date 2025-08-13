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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_generator")
    @SequenceGenerator(
            name = "order_seq_generator",
            sequenceName = "ORDER_SEQ",
            allocationSize = 1
    )
    private Long id;

    @Column(unique = true, nullable = false)
    private String pgOrderId;

    private String paymentKey;

    private Integer refundedAmount;

    private String cancelReason;

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
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, LocalDateTime orderDate, OrderStatus status, String pgOrderId) {
        this.user = user;
        this.orderDate = orderDate;
        this.status = status;
        this.pgOrderId = pgOrderId;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void markAsPaid(String paymentKey) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태의 주문만 결제 완료 처리할 수 있습니다.");
        }
        this.paymentKey = paymentKey;
        this.status = OrderStatus.PAID;
    }


    public void cancel(String reason) {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("결제 완료 상태의 주문만 취소가 가능합니다.");
        }
        
        // 재고 복구 로직 (기존과 동일)
        for (OrderItem orderItem : orderItems) {
            orderItem.getProduct().increaseStock(orderItem.getCount());
        }

        this.status = OrderStatus.CANCELED;
        this.cancelReason = reason;
        this.refundedAmount = calculateTotalAmount(); // 환불액 기록
    }
    public int calculateTotalAmount() {
        return orderItems.stream()
                .mapToInt(item -> item.getOrderPrice() * item.getCount())
                .sum();
    }

    public void processPayment() {
        // 이 주문이 '결제 대기' 상태일 때만 재고를 차감하도록 방어
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태의 주문에 대해서만 결제 처리를 할 수 있습니다.");
        }

        for (OrderItem orderItem : this.orderItems) {
            orderItem.getProduct().decreaseStock(orderItem.getCount());
        }
    }
}