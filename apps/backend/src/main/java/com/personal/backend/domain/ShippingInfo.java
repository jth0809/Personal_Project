package com.personal.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipping_infos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingInfo {

    @Id
    private Long id; // Product와 ID 공유

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Product product; // Order -> Product로 변경

    @Column(nullable = false)
    private String shippingMethod; // 배송 방식 (예: "택배")

    @Column(nullable = false)
    private Integer shippingFee; // 기본 배송비

    private Integer freeShippingThreshold; // 무료 배송 기준 금액

    @Column(nullable = false)
    private String estimatedDeliveryDays; // 예상 배송 소요일

    private String shippingProvider; // 택배사

    @Builder
    public ShippingInfo(Product product, String shippingMethod, Integer shippingFee, Integer freeShippingThreshold, String estimatedDeliveryDays, String shippingProvider) {
        this.product = product;
        this.shippingMethod = shippingMethod;
        this.shippingFee = shippingFee;
        this.freeShippingThreshold = freeShippingThreshold;
        this.estimatedDeliveryDays = estimatedDeliveryDays;
        this.shippingProvider = shippingProvider;
    }

    public void update(String shippingMethod, Integer shippingFee, Integer freeShippingThreshold, String estimatedDeliveryDays, String shippingProvider) {
        this.shippingMethod = shippingMethod;
        this.shippingFee = shippingFee;
        this.freeShippingThreshold = freeShippingThreshold;
        this.estimatedDeliveryDays = estimatedDeliveryDays;
        this.shippingProvider = shippingProvider;
    }
}
