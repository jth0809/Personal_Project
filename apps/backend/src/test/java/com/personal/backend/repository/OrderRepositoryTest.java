package com.personal.backend.repository;

import com.personal.backend.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    private User savedUser;
    private Product savedProduct;

    // 각 테스트가 실행되기 전에 공통적으로 필요한 데이터를 설정합니다.
    @BeforeEach
    void setUp() {
        // given-1: 테스트에 사용할 사용자(User)를 생성하고 저장합니다.
        User user = User.builder()
                .email("orderuser@example.com")
                .password("password")
                .username("주문사용자")
                .role(UserRole.USER)
                .build();
        savedUser = userRepository.save(user);

        // given-2: 테스트에 사용할 상품(Product)을 생성하고 저장합니다.
        Product product = Product.builder()
                .name("테스트용 신발")
                .price(120000)
                .description("편안한 신발입니다.")
                .imageUrl(List.of("shoe.jpg"))
                .build();
        savedProduct = productRepository.save(product);
    }

    @Test
    @DisplayName("주문 저장 및 조회 테스트")
    void saveAndFindOrderTest() {
        // given-3: 주문(Order)과 주문상품(OrderItem)을 생성합니다.
        Order order = Order.builder()
                .user(savedUser)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING) // 주문 상태는 '대기'
                .build();

        OrderItem orderItem = OrderItem.builder()
                .product(savedProduct)
                .orderPrice(savedProduct.getPrice()) // 주문 시점의 가격
                .count(2) // 2개 주문
                .build();
        
        // 주문에 주문상품을 추가합니다. (연관관계 설정)
        order.addOrderItem(orderItem);

        // when: 주문을 데이터베이스에 저장하고, 다시 ID로 조회합니다.
        Order savedOrder = orderRepository.save(order);
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElse(null);

        // then: 저장된 주문과 조회된 주문의 세부 정보가 일치해야 합니다.
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(foundOrder.getUser().getUsername()).isEqualTo("주문사용자");
        
        // then-2: 주문에 포함된 주문상품 정보도 정확해야 합니다.
        assertThat(foundOrder.getOrderItems()).hasSize(1);
        OrderItem foundOrderItem = foundOrder.getOrderItems().get(0);
        assertThat(foundOrderItem.getProduct().getName()).isEqualTo("테스트용 신발");
        assertThat(foundOrderItem.getCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("주문 취소 기능 테스트")
    void cancelOrderTest() {
        // given: 완료(COMPLETED) 상태가 아닌 주문을 하나 생성하여 저장합니다.
        Order order = Order.builder()
                .user(savedUser)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();
        Order savedOrder = orderRepository.save(order);

        // when: 저장된 주문을 조회하여 cancel() 메소드를 호출합니다.
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        foundOrder.cancel();
        // @DataJpaTest는 트랜잭션을 사용하므로, 변경 감지(dirty checking)에 의해
        // save를 명시적으로 호출하지 않아도 업데이트 쿼리가 실행됩니다.

        // then: 주문의 상태가 CANCELED로 변경되었는지 확인합니다.
        Order canceledOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("특정 사용자의 모든 주문 조회 테스트")
    void findByUserTest() {

        Pageable pageable = PageRequest.of(0, 10);
        // given: 다른 사용자(userB)를 추가로 생성
        User userB = User.builder().email("userB@example.com").password("pw").username("유저B").role(UserRole.USER).build();
        userRepository.save(userB);

        // given-2: 각 사용자별로 주문을 생성하여 저장
        // savedUser(테스트 사용자)는 2개의 주문을 가짐
        orderRepository.save(Order.builder().user(savedUser).status(OrderStatus.COMPLETED).build());
        orderRepository.save(Order.builder().user(savedUser).status(OrderStatus.PENDING).build());
        
        // userB는 1개의 주문을 가짐
        orderRepository.save(Order.builder().user(userB).status(OrderStatus.COMPLETED).build());

        // when: 테스트 사용자(savedUser)로 주문 목록을 조회
        Page<Order> ordersOfSavedUser = orderRepository.findByUser(savedUser,pageable);

        // then: 조회된 주문 목록은 2개여야 하며, 모두 savedUser의 주문이어야 함
        assertThat(ordersOfSavedUser).hasSize(2);
        assertThat(ordersOfSavedUser).allMatch(order -> order.getUser().equals(savedUser));
    }
}