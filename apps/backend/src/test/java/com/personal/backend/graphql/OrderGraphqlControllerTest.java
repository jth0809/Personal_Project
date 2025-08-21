package com.personal.backend.graphql;

import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.domain.Order;
import com.personal.backend.domain.OrderStatus;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.CartDto;
import com.personal.backend.dto.OrderDto;
import com.personal.backend.dto.PaymentDto;
import com.personal.backend.graphql.dto.OrderInput;
import com.personal.backend.graphql.dto.OrderItemInput;
import com.personal.backend.repository.OrderRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;
import com.personal.backend.service.CartService;
import com.personal.backend.service.OrderService;
import com.personal.backend.service.PaymentService;
import com.personal.backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderGraphqlControllerTest {

    private GraphQlTester graphQlTester;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CartService cartService;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private PaymentService paymentService;
    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductRepository productRepository;
    @MockitoBean
    private OrderRepository orderRepository;

    private OrderDto.HistoryResponse mockOrderHistory;
    private OrderDto.CreateResponse mockOrderCreateResponse;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        String userEmail = "user@test.com";
        User mockUser = User.builder().email(userEmail).password("password").role(UserRole.USER).build();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));

        String token = jwtTokenProvider.createToken(userEmail);

        WebTestClient client = MockMvcWebTestClient.bindTo(mockMvc)
                .baseUrl("/graphql")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        this.graphQlTester = HttpGraphQlTester.create(client);

        mockOrderHistory = new OrderDto.HistoryResponse(
                1L,
                LocalDateTime.now(),
                OrderStatus.PENDING.name(),
                Collections.emptyList()
        );

        mockOrderCreateResponse = new OrderDto.CreateResponse(
                "pg-order-id-123",
                "Test Product 외 0건",
                10000,
                "user@test.com",
                "user"
        );
        
        // mock(Order.class) 대신 실제 Order 객체에 가까운 Mock을 만듭니다.
        mockOrder = Mockito.mock(Order.class);
        when(mockOrder.getId()).thenReturn(1L);
        when(mockOrder.getPgOrderId()).thenReturn("pg-order-id-123");
        when(mockOrder.getStatus()).thenReturn(OrderStatus.PENDING);
        when(mockOrder.getOrderDate()).thenReturn(LocalDateTime.now());
    }

    // --- Query Resolvers Tests ---

    @Test
    @DisplayName("GraphQL Query: 내 장바구니 조회 - 성공 (myCart)")
    void myCart_query_success() {
        // Given
        CartDto.CartItemResponse cartItem = new CartDto.CartItemResponse(1L, 101L, "Test Product", 10000, "img.jpg", 2);
        CartDto.CartResponse cartResponse = new CartDto.CartResponse(List.of(cartItem), 20000);
        when(cartService.getCartForUser(anyString())).thenReturn(cartResponse);

        Product mockProduct = Mockito.mock(Product.class);
        when(mockProduct.getId()).thenReturn(101L);
        when(mockProduct.getName()).thenReturn("Test Product");
        when(productService.findProductsByIdIn(any())).thenReturn(List.of(mockProduct));

        // When & Then
        graphQlTester.documentName("order")
                .operationName("MyCart") 
                .execute()
                .path("myCart.totalPrice").entity(Integer.class).isEqualTo(20000)
                .path("myCart.items[0].id").entity(String.class).isEqualTo("1")
                .path("myCart.items[0].product.name").entity(String.class).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("GraphQL Query: 내 주문 목록 조회 - 성공 (myOrders)")
    void myOrders_query_success() {
        // Given
        Page<OrderDto.HistoryResponse> orderPage = new PageImpl<>(List.of(mockOrderHistory));
        when(orderService.getOrderHistory(anyString(), any())).thenReturn(orderPage);

        // When & Then
        graphQlTester.documentName("order")
                .operationName("MyOrders") 
                .variable("page", 0)
                .variable("size", 10)
                .execute()
                .path("myOrders.content").entityList(Object.class).hasSize(1)
                .path("myOrders.content[0].id").entity(String.class).isEqualTo("1")
                .path("myOrders.content[0].status").entity(String.class).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("GraphQL Query: 단일 주문 상세 조회 - 성공 (order)")
    void order_query_success() {
        // Given
        when(orderService.findOrderDetails(anyString(), anyLong())).thenReturn(mockOrderHistory);

        // When & Then
        graphQlTester.documentName("order")
                .operationName("Order") 
                .variable("id", 1L)
                .execute()
                .path("order.id").entity(String.class).isEqualTo("1")
                .path("order.status").entity(String.class).isEqualTo("PENDING");
    }

    // --- Mutation Resolvers Tests ---

    @Test
    @DisplayName("GraphQL Mutation: 장바구니에 상품 추가 - 성공 (addItemToCart)")
    void addItemToCart_mutation_success() {
        // Given
        CartDto.CartItemResponse cartItem = new CartDto.CartItemResponse(1L, 101L, "Test Product", 10000, "img.jpg", 1);
        CartDto.CartResponse cartResponse = new CartDto.CartResponse(List.of(cartItem), 10000);
        when(cartService.addItem(anyString(), any(CartDto.AddItemRequest.class))).thenReturn(cartResponse);

        // When & Then
        graphQlTester.documentName("order")
                .operationName("AddItemToCart") 
                .variable("productId", 101L)
                .variable("quantity", 1)
                .execute()
                .path("addItemToCart.totalPrice").entity(Integer.class).isEqualTo(10000);
    }

    @Test
    @DisplayName("GraphQL Mutation: 장바구니 상품 제거 - 성공 (removeCartItem)")
    void removeCartItem_mutation_success() {
        // Given
        CartDto.CartResponse emptyCartResponse = new CartDto.CartResponse(Collections.emptyList(), 0);
        when(cartService.removeItemFromCart(anyString(), anyLong())).thenReturn(emptyCartResponse);


        // When & Then
        graphQlTester.documentName("order")
                .operationName("RemoveCartItem") 
                .variable("cartItemId", 1L)
                .execute()
                .path("removeCartItem.totalPrice").entity(Integer.class).isEqualTo(0)
                .path("removeCartItem.items").entityList(CartDto.CartItemResponse.class).hasSize(0);
    }

    @Test
    @DisplayName("GraphQL Mutation: 주문 생성 - 성공 (createOrder)")
    void createOrder_mutation_success() {
        // Given
        OrderInput orderInput = new OrderInput(List.of(new OrderItemInput(101L, 1)));
        List<OrderDto.OrderItemRequest> orderItemRequests = orderInput.items().stream()
                .map(item -> new OrderDto.OrderItemRequest(item.productId(), item.count()))
                .toList();
        OrderDto.CreateRequest createRequest = new OrderDto.CreateRequest(orderItemRequests);

        when(orderService.createOrder(anyString(), eq(createRequest))).thenReturn(mockOrderCreateResponse);
        
        // ✨ 수정된 부분 시작
        // 1. mock 객체의 메서드 호출 결과를 미리 변수에 저장합니다.
        Long orderIdFromMock = mockOrder.getId();

        // 2. when() 구문 안에서는 mock 객체 대신 변수를 사용합니다.
        when(orderService.findByPgOrderId(mockOrderCreateResponse.pgOrderId())).thenReturn(Optional.of(mockOrder));
        when(orderService.findOrderDetails(anyString(), eq(orderIdFromMock))).thenReturn(mockOrderHistory);
        // ✨ 수정된 부분 끝

        Map<String, Object> inputMap = Map.of(
                "items", orderInput.items().stream()
                        .map(item -> Map.of("productId", item.productId(), "count", item.count()))
                        .toList()
        );

        // When & Then
        graphQlTester.documentName("order")
                .operationName("CreateOrder")
                .variable("input", inputMap)
                .execute()
                .path("createOrder.id").entity(String.class).isEqualTo("1")
                .path("createOrder.status").entity(String.class).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("GraphQL Mutation: 주문 취소 - 성공 (cancelOrder)")
    void cancelOrder_mutation_success() {
        // Given
        OrderDto.HistoryResponse canceledOrder = new OrderDto.HistoryResponse(
                1L,
                LocalDateTime.now(),
                OrderStatus.CANCELED.name(),
                Collections.emptyList()
        );
        when(orderService.cancelOrder(anyString(), anyLong(), anyString())).thenReturn(canceledOrder);

        // When & Then
        graphQlTester.documentName("order")
                .operationName("CancelOrder")
                .variable("orderId", 1L)
                .variable("reason", "No longer needed")
                .execute()
                .path("cancelOrder.id").entity(String.class).isEqualTo("1")
                .path("cancelOrder.status").entity(String.class).isEqualTo(OrderStatus.CANCELED.name());
    }

    @Test
    @DisplayName("GraphQL Mutation: 토스 결제 승인 - 성공 (confirmTossPayment)")
    void confirmTossPayment_mutation_success() {
        // Given
        PaymentDto.ConfirmationResponse confirmationResponse = new PaymentDto.ConfirmationResponse(
                "DONE", "pg-order-id-123", 10000);
        when(paymentService.confirmPayment(any(PaymentDto.VerificationRequest.class)))
                .thenReturn(Mono.just(confirmationResponse));

        // When & Then
        Map<String, Object> inputVariables = Map.of(
                "orderId", "pg-order-id-123",
                "amount", 10000,
                "paymentKey", "paymentKey-123",
                "provider", "TOSS"
        );

        graphQlTester.documentName("order")
                .operationName("ConfirmTossPayment")
                .variable("input", inputVariables)
                .execute()
                .path("confirmTossPayment.orderId").entity(String.class).isEqualTo("pg-order-id-123")
                .path("confirmTossPayment.status").entity(String.class).isEqualTo("DONE");
    }
}