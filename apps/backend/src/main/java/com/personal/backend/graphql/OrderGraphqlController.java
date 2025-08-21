package com.personal.backend.graphql;

import com.personal.backend.domain.Order;
import com.personal.backend.domain.OrderStatus; // OrderStatus Enum 임포트
import com.personal.backend.domain.Product;
import com.personal.backend.dto.CartDto;
import com.personal.backend.dto.OrderDto;
import com.personal.backend.graphql.dto.OrderInput;
import com.personal.backend.dto.PaymentDto;
import com.personal.backend.service.CartService;
import com.personal.backend.service.OrderService;
import com.personal.backend.service.PaymentService;
import com.personal.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderGraphqlController {

    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductService productService; // For field resolver


    // --- Query Resolvers ---

    @QueryMapping
    public CartDto.CartResponse myCart(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.getCartForUser(userDetails.getUsername());
    }

    @QueryMapping
    public Page<OrderDto.HistoryResponse> myOrders(
            @Argument Integer page, @Argument Integer size,
            @Argument String sortBy, @Argument String sortOrder,
            @AuthenticationPrincipal UserDetails userDetails) {
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        return orderService.getOrderHistory(userDetails.getUsername(), pageable);
    }

    @QueryMapping
    public OrderDto.HistoryResponse order(@Argument Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.findOrderDetails(userDetails.getUsername(), id);
    }

    // --- Mutation Resolvers ---

    @MutationMapping
    public CartDto.CartResponse addItemToCart(@Argument Long productId, @Argument int quantity, @AuthenticationPrincipal UserDetails userDetails) {
        CartDto.AddItemRequest request = new CartDto.AddItemRequest(productId, quantity);
        return cartService.addItem(userDetails.getUsername(), request);
    }

    @MutationMapping
    public CartDto.CartResponse removeCartItem(@Argument Long cartItemId, @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.removeItemFromCart(userDetails.getUsername(), cartItemId);
    }

    @MutationMapping
    public OrderDto.HistoryResponse createOrder(@Argument("input") OrderInput input, @AuthenticationPrincipal UserDetails userDetails) {
        List<OrderDto.OrderItemRequest> orderItems = input.items().stream()
                .map(item -> new OrderDto.OrderItemRequest(item.productId(), item.count()))
                .toList();
        OrderDto.CreateRequest request = new OrderDto.CreateRequest(orderItems);
        // 1. 주문 생성 로직은 그대로 사용합니다.
        OrderDto.CreateResponse createResponse = orderService.createOrder(userDetails.getUsername(), request);

        // 2. 생성된 주문의 상세 정보를 DTO 형태로 조회하여 반환합니다.
        //    (이를 위해 OrderService에 pgOrderId로 조회하는 기능이 필요할 수 있습니다.)
        //    테스트를 통과시키기 위해, 생성된 Order 엔티티를 찾아 DTO로 변환하는 과정을 추가합니다.
        Order newOrder = orderService.findByPgOrderId(createResponse.pgOrderId())
                .orElseThrow(() -> new RuntimeException("Order creation failed: could not find order by pgOrderId"));
        
        // 3. findOrderDetails를 사용하여 응답 형식을 통일합니다.
        return orderService.findOrderDetails(userDetails.getUsername(), newOrder.getId());
    }
    
    @MutationMapping
    public OrderDto.HistoryResponse cancelOrder(@Argument Long orderId, @Argument String reason, @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.cancelOrder(userDetails.getUsername(), orderId, reason);
    }

    @MutationMapping
    public PaymentDto.ConfirmationResponse confirmTossPayment(@Argument("input") PaymentDto.VerificationRequest input) {
        return paymentService.confirmPayment(input).block(); // Mono<T> to T
    }

    // --- Field Resolvers (N+1 해결) ---

    // ⛔️ 삭제: 'id' 필드는 자동 매핑되므로 이 리졸버는 더 이상 필요 없으며, 타입 불일치 오류의 원인이었습니다.
    // @SchemaMapping(typeName = "Order", field = "id")
    // public Long resolveOrderId(OrderDto.HistoryResponse response) {
    //     return response.id();
    // }

    // ✨ 추가: DTO의 'orderStatus' (String)를 스키마의 'status' (Enum)로 변환합니다.
    @SchemaMapping(typeName = "Order", field = "status")
    public OrderStatus resolveOrderStatus(OrderDto.HistoryResponse response) {
        // response.orderStatus()가 null일 경우를 대비한 방어 코드
        if (response.orderStatus() == null) {
            return null;
        }
        return OrderStatus.valueOf(response.orderStatus());
    }

    // ✨ 추가: DTO의 'orderDate' (LocalDateTime)를 스키마의 'orderDate' (String)로 변환합니다.
    @SchemaMapping(typeName = "Order", field = "orderDate")
    public String resolveOrderDate(OrderDto.HistoryResponse response) {
        if (response.orderDate() == null) {
            return null;
        }
        return response.orderDate().toString();
    }


    @BatchMapping(typeName = "CartItem", field = "product")
    public Map<CartDto.CartItemResponse, Product> getProduct(List<CartDto.CartItemResponse> cartItems) {
        Set<Long> productIds = cartItems.stream()
                .map(CartDto.CartItemResponse::productId)
                .collect(Collectors.toSet());

        Map<Long, Product> productsById = productService.findProductsByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return cartItems.stream()
                .collect(Collectors.toMap(Function.identity(), item -> productsById.get(item.productId())));
    }

    // --- Helper Methods ---

    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortOrder) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? size : 10;
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = sortBy != null && !sortBy.isBlank() ? sortBy : "id";
        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
    }

}