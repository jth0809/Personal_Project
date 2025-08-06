package com.personal.backend.repository;

import com.personal.backend.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Optional; 

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private User savedUser;
    private Product savedProduct1;
    private Product savedProduct2;

    @BeforeEach
    void setUp() {
        // given-1: 테스트에 사용할 사용자(User)를 생성하고 저장합니다.
        User user = User.builder()
                .email("cartuser@example.com")
                .password("password")
                .username("장바구니사용자")
                .role(UserRole.USER)
                .build();
        savedUser = userRepository.save(user);

        // given-2: 테스트에 사용할 상품(Product)들을 생성하고 저장합니다.
        Product product1 = Product.builder()
                .name("테스트용 티셔츠")
                .price(30000)
                .description("편안한 티셔츠입니다.")
                .imageUrl("tshirt.jpg")
                .build();
        savedProduct1 = productRepository.save(product1);

        Product product2 = Product.builder()
                .name("테스트용 바지")
                .price(45000)
                .description("편안한 바지입니다.")
                .imageUrl("pants.jpg")
                .build();
        savedProduct2 = productRepository.save(product2);
    }

    @Test
    @DisplayName("장바구니 생성 및 아이템 추가 테스트")
    void saveCartWithItemsTest() {
        // given-3: 사용자와 연결된 장바구니(Cart)를 생성합니다.
        Cart cart = Cart.builder()
                .user(savedUser)
                .build();

        // given-4: 장바구니에 담을 아이템(CartItem)들을 생성합니다.
        // Cart 엔티티의 cartItems 필드에 CascadeType.ALL이 적용되어 있으므로,
        // CartItem들을 Cart의 리스트에 추가하고 Cart만 저장해도 CartItem들이 함께 저장됩니다.
        CartItem item1 = CartItem.builder()
                .cart(cart)
                .product(savedProduct1)
                .quantity(2) // 티셔츠 2개
                .build();

        CartItem item2 = CartItem.builder()
                .cart(cart)
                .product(savedProduct2)
                .quantity(1) // 바지 1개
                .build();

        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);

        // when: 장바구니를 저장하고 다시 조회합니다.
        Cart savedCart = cartRepository.save(cart);
        Cart foundCart = cartRepository.findById(savedCart.getId()).orElse(null);

        // then: 조회된 장바구니와 그 안의 아이템 정보가 정확해야 합니다.
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getUser().getUsername()).isEqualTo("장바구니사용자");
        assertThat(foundCart.getCartItems()).hasSize(2);

        // then-2: 각 아이템의 정보가 정확한지 확인합니다.
        assertThat(foundCart.getCartItems())
                .extracting("product.name", "quantity") // 상품명과 수량만 추출하여
                .containsExactlyInAnyOrder( // 순서에 상관없이 내용이 일치하는지 검사
                        tuple("테스트용 티셔츠", 2),
                        tuple("테스트용 바지", 1)
                );
    }

    // 👇 추가된 테스트 메소드
    @Test
    @DisplayName("사용자로 장바구니 조회 테스트")
    void findByUserTest() {
        // given: 테스트 사용자(savedUser)의 장바구니를 생성 및 저장
        Cart cart = Cart.builder().user(savedUser).build();
        cartRepository.save(cart);

        // when: savedUser로 장바구니를 조회
        Optional<Cart> foundCartOptional = cartRepository.findByUser(savedUser);

        // then: 조회된 장바구니가 존재하며, 해당 사용자의 장바구니가 맞는지 확인
        assertThat(foundCartOptional).isPresent();
        assertThat(foundCartOptional.get().getUser()).isEqualTo(savedUser);
    }
    
    // 👇 추가된 테스트 메소드
    @Test
    @DisplayName("장바구니가 없는 사용자로 조회 시 빈 결과 반환 테스트")
    void findByUserWhenCartNotExistsTest() {
        // given: 장바구니를 생성하지 않은 새로운 사용자
        User userWithoutCart = userRepository.save(User.builder().email("no-cart@user.com").password("pw").username("노카트").role(UserRole.USER).build());

        // when: 해당 사용자로 장바구니를 조회
        Optional<Cart> foundCartOptional = cartRepository.findByUser(userWithoutCart);

        // then: 조회 결과는 비어 있어야 함
        assertThat(foundCartOptional).isEmpty();
    }
}