package com.personal.backend.domain;

// public으로 선언하여, 다른 패키지(service, controller 등)에서도 접근할 수 있도록 합니다.
public enum OrderStatus {
    PENDING,   // 주문 대기
   COMPLETED, // 주문 완료
    CANCELED   // 주문 취소
}
