package com.personal.backend.service;

import com.personal.backend.domain.Cart;
import com.personal.backend.domain.CartItem;
import com.personal.backend.domain.Order;
import com.personal.backend.domain.OrderItem;
import com.personal.backend.domain.OrderStatus;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;
import com.personal.backend.dto.OrderDto;
import com.personal.backend.payment.PaymentGateway;
import com.personal.backend.repository.CartRepository;
import com.personal.backend.repository.OrderRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final PaymentGateway paymentGateway;
    // 실제 구현에서는 UserRepository, ProductRepository 등도 필요합니다.

    public Page<OrderDto.HistoryResponse> getOrderHistory(String userEmail, Pageable pageable) {
        //현재 사용자의 주문 내역을 DB에서 조회하여 DTO로 변환하는 로직
        if (pageable.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by(Sort.Direction.DESC, "orderDate");
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }
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
    public OrderDto.CreateResponse createOrder(String userEmail, OrderDto.CreateRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 주문 상품 목록 생성 (내부적으로 상품 조회 및 재고 확인)
        List<OrderItem> orderItems = createOrderItems(request.orderItems());
        
        // 3. 'PENDING' 상태의 주문 엔티티 생성 및 저장
        Order order = createAndSavePendingOrder(user, orderItems);
        
        // 4. 프론트엔드에 전달할 DTO 생성
        String orderName = generateOrderName(orderItems);
        int totalAmount = calculateTotalPrice(orderItems);

        clearCartItems(user, orderItems);

        return new OrderDto.CreateResponse(
                order.getPgOrderId(),
                orderName,
                totalAmount,
                user.getEmail(),
                user.getUsername()
        );
    }

    private List<OrderItem> createOrderItems(List<OrderDto.OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(itemRequest -> {
                    Product product = productRepository.findByIdWithPessimisticLock(itemRequest.productId())
                            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. id=" + itemRequest.productId()));
                    
                    if (product.getStockQuantity() < itemRequest.count()) {
                        throw new IllegalStateException("재고가 부족합니다. (상품명: " + product.getName() + ")");
                    }

                    return OrderItem.builder()
                            .product(product)
                            .orderPrice(product.getPrice())
                            .count(itemRequest.count())
                            .build();
                })
                .toList();
    }

    private Order createAndSavePendingOrder(User user, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .pgOrderId(UUID.randomUUID().toString())
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        return orderRepository.save(order);
    }

    private int calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToInt(item -> item.getOrderPrice() * item.getCount())
                .sum();
    }

    private String generateOrderName(List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) return "주문 상품 없음";
        String firstProductName = orderItems.get(0).getProduct().getName();
        return orderItems.size() > 1 ? firstProductName + " 외 " + (orderItems.size() - 1) + "건" : firstProductName;
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
    public OrderDto.HistoryResponse cancelOrder(String userEmail, Long orderId, String cancelReason) {
        // 1. 사용자 및 주문 정보를 조회하고, 취소 권한이 있는지 확인합니다.
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 주문을 취소할 권한이 없습니다.");
        }

        // 2. PaymentGateway를 통해 PG사에 '결제 취소'를 먼저 요청합니다.
        //    이 과정이 실패하면(예: 이미 취소된 결제), 예외가 발생하여 아래 로직은 실행되지 않습니다.
        paymentGateway.cancel(order.getPaymentKey(), cancelReason)
                      .block(); // 비동기 작업이 완료될 때까지 동기적으로 기다립니다.

        // 3. PG사 환불이 성공적으로 완료되었을 때만, 우리 DB의 주문 상태를 변경하고 재고를 복구합니다.
        //    Order 엔티티가 모든 관련 비즈니스 로직을 직접 처리합니다.
        order.cancel(cancelReason);

        // 4. 변경된 최종 주문 상태를 DTO로 변환하여 반환합니다.
        //    @Transactional에 의해 이 메소드가 끝나면 order의 변경사항이 DB에 자동으로 저장됩니다.
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

    @Transactional(readOnly = true)
    public int getOrderAmountByPgOrderId(String pgOrderId) {
        Order order = orderRepository.findByPgOrderId(pgOrderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));

        // 주문에 포함된 모든 상품의 가격 * 수량을 합산하여 반환
        return order.getOrderItems().stream()
                .mapToInt(item -> item.getOrderPrice() * item.getCount())
                .sum();
    }

    /**
     * PG사 주문 ID로 주문을 찾아 상태를 '결제 완료'로 변경합니다.
     */
    @Transactional
    public void markOrderAsPaid(String pgOrderId, String paymentKey) {
        Order order = orderRepository.findByPgOrderId(pgOrderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));

        order.markAsPaid(paymentKey); // Order 엔티티의 비즈니스 메소드 호출
    }

    @Transactional
    public void processPostPayment(String pgOrderId) {
    // 1. pgOrderId로 방금 결제가 완료된 주문(Order)을 찾습니다.
        Order order = orderRepository.findByPgOrderId(pgOrderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        
        order.processPayment();
    }

    private void clearCartItems(User user, List<OrderItem> orderItems) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 정보를 찾을 수 없습니다."));

        List<Long> orderedProductIds = orderItems.stream()
                .map(item -> item.getProduct().getId())
                .toList();
                
        cart.removeItems(orderedProductIds);
        cartRepository.save(cart);
    }
}

