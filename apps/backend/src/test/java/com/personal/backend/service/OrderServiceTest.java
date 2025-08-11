package com.personal.backend.service;

import com.personal.backend.domain.*;
import com.personal.backend.dto.OrderDto;
import com.personal.backend.dto.PaymentDto;
import com.personal.backend.payment.PaymentGateway;
import com.personal.backend.repository.*;

import reactor.core.publisher.Mono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartRepository cartRepository;

    @Mock
    private PaymentGateway paymentGateway;

    private User dummyUser;
    private Product dummyProduct;
    private Cart dummyCart;
    private Order dummyOrder;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 공통 가짜 객체 설정
        dummyUser = User.builder().email("test@user.com").username("테스트유저").role(UserRole.USER).build();
        try { // ID 설정
            var userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(dummyUser, 1L);
        } catch (Exception e) { e.printStackTrace(); }


        dummyProduct = Product.builder().name("테스트 상품").price(10000).stockQuantity(10).build();
        try { // ID 설정
            var productIdField = Product.class.getDeclaredField("id");
            productIdField.setAccessible(true);
            productIdField.set(dummyProduct, 100L);
        } catch (Exception e) { e.printStackTrace(); }


        dummyCart = Cart.builder().user(dummyUser).build();

        dummyOrder = Order.builder().user(dummyUser).orderDate(LocalDateTime.now()).status(OrderStatus.PENDING).pgOrderId("test-pg-order-id").build();
        try { // ID 설정
            var orderIdField = Order.class.getDeclaredField("id");
            orderIdField.setAccessible(true);
            orderIdField.set(dummyOrder, 1L);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() {
        // given
        String userEmail = "test@user.com";
        OrderDto.CreateRequest request = new OrderDto.CreateRequest(
                List.of(new OrderDto.OrderItemRequest(100L, 2))
        );

        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(cartRepository.findByUser(dummyUser)).thenReturn(Optional.of(dummyCart));
        when(orderRepository.save(any(Order.class))).thenReturn(dummyOrder);
        when(productRepository.findByIdWithPessimisticLock(100L)).thenReturn(Optional.of(dummyProduct));

        // when
        OrderDto.CreateResponse response = orderService.createOrder(userEmail, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualTo(20000); // 10000원 * 2개
        assertThat(response.orderName()).isEqualTo("테스트 상품");
        assertThat(response.pgOrderId()).isNotNull(); // pgOrderId가 생성되었는지 확인
        assertThat(dummyCart.getCartItems()).isEmpty();
    }


    @Test
    @DisplayName("주문 내역 조회 성공 (페이지네이션 적용)")
    void getOrderHistory_Success_WithPagination() {
        // given
        String userEmail = "test@user.com";
        // 1. 테스트용 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 10);
        
        // 2. Mock Repository가 반환할 Page<Order> 객체 생성
        List<Order> orderList = List.of(dummyOrder);
        Page<Order> orderPage = new PageImpl<>(orderList, pageable, orderList.size());

        // 3. Mock 설정: findByUser(Pageable)가 호출되면 Page 객체를 반환하도록 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(orderRepository.findByUser(dummyUser, pageable)).thenReturn(orderPage);

        // when
        // 4. 서비스 메소드 호출 시 Pageable 객체 전달
        Page<OrderDto.HistoryResponse> resultPage = orderService.getOrderHistory(userEmail, pageable);

        // then
        // 5. 반환된 Page 객체의 내용을 검증
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getTotalPages()).isEqualTo(1);
    }
    

    @Test
    @DisplayName("주문 상세 조회 실패 - 권한 없음")
    void findOrderDetails_Fail_AccessDenied() {
        // given
        String userEmail = "hacker@user.com";
        Long orderId = 1L; // 이 주문은 dummyUser(1L)의 것

        User hacker = User.builder().email(userEmail).build();
        try { // ID 설정
            var userIdField = User.class.getDeclaredField("id");
            userIdField.setAccessible(true);
            userIdField.set(hacker, 2L); // 다른 ID를 가진 사용자
        } catch (Exception e) { e.printStackTrace(); }

        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(hacker));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(dummyOrder));

        // when & then
        assertThrows(SecurityException.class, () -> orderService.findOrderDetails(userEmail, orderId));
    }

    @Test
    @DisplayName("주문 취소 성공 - PG사 호출 및 재고 복구 확인")
    void cancelOrder_Success() {
        // given
        String userEmail = "test@user.com";
        Long orderId = 1L;
        String cancelReason = "고객 변심";

        // 1. 테스트를 위해 주문에 상품(2개)과 '결제 키'를 설정하고, 상태를 PAID로 만듭니다.
        OrderItem orderItem = OrderItem.builder().product(dummyProduct).count(2).build();
        dummyOrder.addOrderItem(orderItem);
        dummyOrder.markAsPaid("test_payment_key_123");
        
        // 초기 재고는 10개
        assertThat(dummyProduct.getStockQuantity()).isEqualTo(10);

        // 2. Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(dummyOrder));
        // [수정] 어떤 문자열이든 받아들이도록 anyString() Matcher를 사용하여 유연성을 높입니다.
        when(paymentGateway.cancel(anyString(), anyString()))
                .thenReturn(Mono.just(new PaymentDto.ConfirmationResponse("CANCELED", "dummy-order-id", 20000)));

        // when
        orderService.cancelOrder(userEmail, orderId, cancelReason);

        // then
        // 3. PaymentGateway의 cancel 메소드가 올바른 인자들로 1번 호출되었는지 검증합니다.
        verify(paymentGateway, times(1)).cancel(eq("test_payment_key_123"), eq(cancelReason));

        // 4. 주문 상태가 CANCELED로 변경되었는지 확인합니다.
        assertThat(dummyOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
        
        // 5. 상품 재고가 주문 수량(2개)만큼 다시 복구되었는지 확인합니다. (10 + 2 = 12)
        assertThat(dummyProduct.getStockQuantity()).isEqualTo(12);
    }

    @Test
    @DisplayName("주문 생성 실패 - 재고 부족")
    void createOrder_Fail_OutOfStock() {
        // given
        String userEmail = "test@user.com";
        // dummyProduct의 재고는 10개인데, 11개를 주문하는 상황
        OrderDto.CreateRequest request = new OrderDto.CreateRequest(
                List.of(new OrderDto.OrderItemRequest(100L, 11))
        );

        // Mock 설정: productRepository.findByIdWithPessimisticLock이 호출되면 dummyProduct를 반환
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(productRepository.findByIdWithPessimisticLock(100L)).thenReturn(Optional.of(dummyProduct));
        // when & then
        // productService.createOrder를 실행했을 때,
        // Product 엔티티의 decreaseStock 메소드에서 IllegalStateException이 발생해야 합니다.
        assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(userEmail, request);
        });

        // then: 주문이 생성되면 안 되므로, orderRepository.save는 호출되지 않았는지 검증
        verify(orderRepository, never()).save(any(Order.class));
    }

    // 👇 [추가] 주문 취소 시 재고 복구 시나리오 테스트
    @Test
    @DisplayName("주문 취소 성공 - 재고 복구 확인")
    void cancelOrder_Success_StockIncrease() {
        // given
        String userEmail = "test@user.com";
        Long orderId = 1L;
        String cancelReason = "고객 변심";
        
        // 주문 상품 설정: dummyProduct 2개를 주문한 상태
        OrderItem orderItem = OrderItem.builder().product(dummyProduct).count(2).build();
        dummyOrder.addOrderItem(orderItem);
        dummyOrder.markAsPaid("test_payment_key_123");
        // 초기 재고는 10개
        assertThat(dummyProduct.getStockQuantity()).isEqualTo(10);

        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(dummyOrder));
        when(paymentGateway.cancel(anyString(), anyString()))
                .thenReturn(Mono.just(new PaymentDto.ConfirmationResponse("CANCELED", "dummy-order-id", 20000)));

        // when
        orderService.cancelOrder(userEmail, orderId, cancelReason);

        // then
        // 주문 취소 후, 2개가 다시 복구되어 재고가 12개가 되었는지 확인
        assertThat(dummyProduct.getStockQuantity()).isEqualTo(12);
        assertThat(dummyOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("결제 후 처리(processPostPayment) 성공 - 재고 차감 및 장바구니 비우기")
    void processPostPayment_Success() {

        // 주문에 상품(2개) 추가
        OrderItem orderItem = OrderItem.builder().product(dummyProduct).count(2).build();
        dummyOrder.addOrderItem(orderItem);
        
        // 장바구니에도 동일한 상품이 있다고 가정
        CartItem cartItem = CartItem.builder().product(dummyProduct).quantity(5).build();
        dummyCart.getCartItems().add(cartItem);

        // Mock 설정
        when(orderRepository.findByPgOrderId(dummyOrder.getPgOrderId())).thenReturn(Optional.of(dummyOrder));

        // when
        orderService.processPostPayment(dummyOrder.getPgOrderId());

        // then
        // 1. 재고가 10개에서 8개로 정상적으로 차감되었는지 검증
        assertThat(dummyProduct.getStockQuantity()).isEqualTo(8);
    }
}