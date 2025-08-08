package com.personal.backend.service;

import com.personal.backend.domain.Cart;
import com.personal.backend.domain.Order;
import com.personal.backend.domain.OrderItem;
import com.personal.backend.domain.OrderStatus;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;
import com.personal.backend.dto.OrderDto;
import com.personal.backend.repository.CartRepository;
import com.personal.backend.repository.OrderRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    // 실제 구현에서는 UserRepository, ProductRepository 등도 필요합니다.

    public Page<OrderDto.HistoryResponse> getOrderHistory(String userEmail, Pageable pageable) {
        //현재 사용자의 주문 내역을 DB에서 조회하여 DTO로 변환하는 로직
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Page<Order> ordersPage = orderRepository.findByUser(user, pageable);
        return ordersPage.map(order -> new OrderDto.HistoryResponse( // 각 Order 객체를 HistoryResponse DTO로 매핑
                        order.getId(),
                        order.getOrderDate(),
                        order.getStatus().name(),
                        order.getOrderItems().stream()
                                .map(orderItem -> new OrderDto.OrderItemResponse(
                                        orderItem.getProduct().getName(), // 연관된 상품 엔티티에서 이름 가져오기
                                        orderItem.getCount(),
                                        orderItem.getOrderPrice()
                                ))
                                .toList() // 결과를 List<OrderItemResponse>로 수집
                    ));
    }

    @Transactional
    public Long createOrder(String userEmail, OrderDto.CreateRequest request) {
        // 1. 요청된 상품 ID들로 상품 정보 조회
        // 2. 재고 확인 및 가격 계산
        // 3. Order 및 OrderItem 엔티티 생성
        // 4. Repository를 통해 DB에 저장
        // 5. 장바구니 비우기
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 정보를 찾을 수 없습니다."));

                
        List<OrderItem> orderItems = request.orderItems().stream()
                .map(itemRequest -> {
                    // 2-1. 상품 ID로 Product 엔티티를 조회합니다.
                    Product product = productRepository.findById(itemRequest.productId())
                            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + itemRequest.productId()));
                    
                    // 2-2. 조회된 상품 정보로 OrderItem을 생성합니다.
                    //      (실제로는 재고 확인 로직 등이 추가되어야 합니다.)
                    return OrderItem.builder()
                            .product(product)
                            .orderPrice(product.getPrice()) // 주문 시점의 상품 가격을 기록
                            .count(itemRequest.count())
                            .build();
                })
                .toList();
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING) // 초기 상태는 '주문 대기'
                .build();
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        Order savedOrder = orderRepository.save(order);

        cart.clearItems();
        cartRepository.save(cart);

        return savedOrder.getId();
    }

    public OrderDto.HistoryResponse findOrderDetails(String userEmail, Long orderId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        // 보안 검증: 이 주문이 정말 해당 사용자의 것인지 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 주문에 접근할 권한이 없습니다.");
        }

        // 기존 DTO를 재활용하여 변환
        return convertOrderToHistoryResponse(order);
    }

    @Transactional
    public OrderDto.HistoryResponse cancelOrder(String userEmail, Long orderId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        // 보안 검증
        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 주문을 취소할 권한이 없습니다.");
        }

        // 엔티티의 비즈니스 메소드를 호출하여 상태 변경
        order.cancel();
        
        // 변경된 주문 상태를 DTO로 변환하여 즉시 반환합니다.
        return convertOrderToHistoryResponse(order);
    }

    private OrderDto.HistoryResponse convertOrderToHistoryResponse(Order order) {
        return new OrderDto.HistoryResponse(
            order.getId(),
            order.getOrderDate(),
            order.getStatus().name(),
            order.getOrderItems().stream()
                    .map(orderItem -> new OrderDto.OrderItemResponse(
                            orderItem.getProduct().getName(),
                            orderItem.getCount(),
                            orderItem.getOrderPrice()
                    ))
                    .toList()
        );
    }
}

