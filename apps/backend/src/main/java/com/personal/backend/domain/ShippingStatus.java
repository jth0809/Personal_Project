package com.personal.backend.domain;

public enum ShippingStatus {
    PREPARING,      // 배송 준비중
    SHIPPED,        // 배송중
    DELIVERED,      // 배송 완료
    CANCELED        // 주문 취소로 인한 배송 취소
}