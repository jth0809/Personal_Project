package com.personal.backend.datainit;

import com.oracle.bmc.apmtraces.model.ObjectStorage;
import com.personal.backend.domain.User;
import com.personal.backend.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// 1. 실제 애플리케이션 실행과 유사한 통합 테스트 환경을 설정합니다.
// 2. args 속성으로 "--init-data" 인자를 전달하여 DataInitializer가 실행되도록 합니다.
@SpringBootTest(args = "--init-data")
// 3. 테스트가 끝난 후 모든 DB 변경사항을 롤백하여 테스트 환경을 깨끗하게 유지합니다.
@Transactional
@ActiveProfiles("test")
class DataInitializerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("데이터 초기화 스크립트 실행 후 상태 검증")
    void dataInitialization_VerificationTest() {
        // DataInitializer의 run() 메소드는 @SpringBootTest가 실행될 때 자동으로 완료됩니다.
        // 이 테스트 메소드에서는 그 결과가 올바른지만 확인합니다.

        // --- 1. 사용자 데이터 검증 ---
        assertThat(userRepository.count()).isEqualTo(2); // 관리자, 일반사용자 총 2명
        User user = userRepository.findByEmail("user@test.com").orElseThrow();
        assertThat(user.getUsername()).isEqualTo("일반사용자");

        // --- 2. 카테고리 및 상품 데이터 검증 ---
        assertThat(categoryRepository.count()).isEqualTo(8); // 컴퓨터, 의류, 도서
        assertThat(productRepository.count()).isEqualTo(7); // 총 7개의 상품

        // --- 3. 장바구니 데이터 검증 ---
        // '일반사용자'의 장바구니를 조회하여 아이템이 2종류 있는지 확인
        cartRepository.findByUser(user).ifPresent(cart -> {
            assertThat(cart.getCartItems()).hasSize(2);
            // 아이템 수량의 총합을 확인 (모니터 1개 + 청바지 2개 = 3개)
            int totalQuantity = cart.getCartItems().stream().mapToInt(item -> item.getQuantity()).sum();
            assertThat(totalQuantity).isEqualTo(3);
        });
        
        // --- 4. 주문 데이터 검증 ---
        // '일반사용자'의 주문이 1개 생성되었는지 확인
        assertThat(orderRepository.findByUser(user)).hasSize(1);
    }
}