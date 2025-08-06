package com.personal.backend.config.data;

import com.personal.backend.domain.*;
import com.personal.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final Dataproperties dataproperties;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!Arrays.asList(args).contains("--init-data")) {
            log.info("No '--init-data' argument found. Skipping data initialization.");
            return;
        }

        log.info("Command line argument '--init-data' detected. Starting data initialization...");

        // --- 1. 사용자 데이터 초기화 ---
        createUsers();

        // --- 2. 카테고리 및 상품 데이터 초기화 ---
        createCategoriesAndProducts();

        // --- 3. 특정 사용자의 장바구니 및 주문 데이터 초기화 ---
        createCartAndOrderForUser();

        log.info("Data initialization finished.");
    }

    private void createUsers() {
        if (userRepository.findByEmail("admin@test.com").isEmpty()) {
            userRepository.save(User.builder()
                    .email("admin@test.com")
                    .password(passwordEncoder.encode(dataproperties.password()))
                    .username("관리자")
                    .role(UserRole.ADMIN)
                    .build());
            log.info("Admin user created: admin@test.com");
        }

        if (userRepository.findByEmail("user@test.com").isEmpty()) {
            userRepository.save(User.builder()
                    .email("user@test.com")
                    .password(passwordEncoder.encode(dataproperties.password()))
                    .username("일반사용자")
                    .role(UserRole.USER)
                    .build());
            log.info("Normal user created: user@test.com");
        }
    }

    private void createCategoriesAndProducts() {
        Category computers = categoryRepository.findByName("컴퓨터")
                .orElseGet(() -> categoryRepository.save(new Category("컴퓨터")));
        Category clothes = categoryRepository.findByName("의류")
                .orElseGet(() -> categoryRepository.save(new Category("의류")));
        Category books = categoryRepository.findByName("도서")
                .orElseGet(() -> categoryRepository.save(new Category("도서")));

        List<Product> products = List.of(
            new Product("고성능 노트북", "최신 M4 칩이 탑재된 노트북입니다.", 2500000, "images/laptop.jpg", computers),
            new Product("기계식 키보드", "타건감이 뛰어난 기계식 키보드입니다.", 120000, "images/keyboard.jpg", computers),
            new Product("QHD 모니터", "27인치 고해상도 모니터입니다.", 350000, "images/monitor.jpg", computers),
            new Product("편안한 반팔 티셔츠", "100% 순면으로 만든 부드러운 티셔츠입니다.", 25000, "images/tshirt.jpg", clothes),
            new Product("데님 청바지", "어디에나 잘 어울리는 클래식한 청바지입니다.", 79000, "images/jeans.jpg", clothes),
            new Product("스프링 부트 완벽 가이드", "실무 예제로 배우는 스프링 부트의 모든 것.", 38000, "images/spring_book.jpg", books),
            new Product("JPA 프로그래밍 입문", "자바 ORM 표준 기술을 익혀보세요.", 35000, "images/jpa_book.jpg", books)
        );

        for (Product product : products) {
            if (productRepository.findByName(product.getName()).isEmpty()) {
                productRepository.save(product);
                log.info("Product created: {}", product.getName());
            }
        }
    }

    /**
     * 'user@test.com' 사용자에 대한 장바구니와 주문 데이터를 생성합니다.
     */
    private void createCartAndOrderForUser() {
        Optional<User> userOpt = userRepository.findByEmail("user@test.com");
        if (userOpt.isEmpty()) {
            log.warn("Cannot create cart/order data because user 'user@test.com' was not found.");
            return;
        }
        User user = userOpt.get();

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
        if (cart.getCartItems().isEmpty()) {
            // 👇 [수정] .stream().findFirst()를 추가하여 Optional로 변환합니다.
            productRepository.findByName("QHD 모니터").stream().findFirst().ifPresent(product -> {
                cart.getCartItems().add(CartItem.builder().cart(cart).product(product).quantity(1).build());
                log.info("Added 'QHD 모니터' to cart for user@test.com");
            });
            // 👇 [수정] .stream().findFirst()를 추가하여 Optional로 변환합니다.
            productRepository.findByName("데님 청바지").stream().findFirst().ifPresent(product -> {
                cart.getCartItems().add(CartItem.builder().cart(cart).product(product).quantity(2).build());
                log.info("Added '데님 청바지' to cart for user@test.com");
            });
            cartRepository.save(cart);
        }

        if (orderRepository.findByUser(user).isEmpty()) {
            // 👇 [수정] .stream().findFirst()를 추가하여 Optional로 변환합니다.
            Optional<Product> laptopOpt = productRepository.findByName("고성능 노트북").stream().findFirst();
            Optional<Product> bookOpt = productRepository.findByName("스프링 부트 완벽 가이드").stream().findFirst();

            if (laptopOpt.isPresent() && bookOpt.isPresent()) {
                Order order = Order.builder()
                        .user(user)
                        .orderDate(LocalDateTime.now().minusDays(7))
                        .status(OrderStatus.COMPLETED)
                        .build();

                OrderItem laptopItem = OrderItem.builder().order(order).product(laptopOpt.get()).count(1).orderPrice(2450000).build();
                OrderItem bookItem = OrderItem.builder().order(order).product(bookOpt.get()).count(1).orderPrice(38000).build();

                order.getOrderItems().addAll(List.of(laptopItem, bookItem));
                orderRepository.save(order);
                log.info("Created a sample order for user@test.com");
            }
        }
    }
}