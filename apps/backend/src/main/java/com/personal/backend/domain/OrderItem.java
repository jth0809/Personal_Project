package com.personal.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_items_seq_generator")
    @SequenceGenerator(
            name = "order_items_seq_generator",
            sequenceName = "ORDER_ITEMS_SEQ",
            allocationSize = 1
    )
    private Long id;

    // 어떤 주문에 속하는지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 어떤 상품이 주문되었는지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int orderPrice; // 주문 당시의 가격 (할인 등 변동 가능성)
    private int count;      // 주문 수량

    @Builder
    public OrderItem(Order order, Product product, int orderPrice, int count) {
        this.order = order;
        this.product = product;
        this.orderPrice = orderPrice;
        this.count = count;
    }
    void setOrder(Order order) {
        this.order = order;
    }
}