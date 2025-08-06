package com.personal.backend.service;

import com.personal.backend.domain.*;
import com.personal.backend.dto.OrderDto;
import com.personal.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    // CartItemRepository는 이 테스트에서 직접 호출되지 않으므로 Mock 객체는 필요 없습니다.

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


        dummyProduct = Product.builder().name("테스트 상품").price(10000).build();
        try { // ID 설정
            var productIdField = Product.class.getDeclaredField("id");
            productIdField.setAccessible(true);
            productIdField.set(dummyProduct, 100L);
        } catch (Exception e) { e.printStackTrace(); }


        dummyCart = Cart.builder().user(dummyUser).build();

        dummyOrder = Order.builder().user(dummyUser).orderDate(LocalDateTime.now()).status(OrderStatus.PENDING).build();
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
        when(productRepository.findById(100L)).thenReturn(Optional.of(dummyProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(dummyOrder);

        // when
        Long createdOrderId = orderService.createOrder(userEmail, request);

        // then
        assertThat(createdOrderId).isEqualTo(1L);
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(productRepository, times(1)).findById(100L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(dummyCart); // 장바구니 비우기 후 저장 호출 확인
    }

    @Test
    @DisplayName("주문 내역 조회 성공")
    void getOrderHistory_Success() {
        // given
        String userEmail = "test@user.com";
        // 주문에 주문 아이템 추가
        OrderItem orderItem = OrderItem.builder().product(dummyProduct).count(2).orderPrice(10000).build();
        dummyOrder.addOrderItem(orderItem);

        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(orderRepository.findByUser(dummyUser)).thenReturn(List.of(dummyOrder));

        // when
        List<OrderDto.HistoryResponse> history = orderService.getOrderHistory(userEmail);

        // then
        assertThat(history).hasSize(1);
        assertThat(history.get(0).orderItems()).hasSize(1);
        assertThat(history.get(0).orderItems().get(0).productName()).isEqualTo("테스트 상품");
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
    @DisplayName("주문 취소 성공")
    void cancelOrder_Success() {
        // given
        String userEmail = "test@user.com";
        Long orderId = 1L;

        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(dummyOrder));

        // when
        OrderDto.HistoryResponse response = orderService.cancelOrder(userEmail, orderId);

        // then
        // Order 엔티티의 cancel() 메소드가 호출되어 상태가 CANCELED로 변경되었는지 확인
        assertThat(dummyOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(response.orderStatus()).isEqualTo(OrderStatus.CANCELED.name());
    }
}