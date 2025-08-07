package com.personal.backend.repository;

import com.personal.backend.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private Cart savedCart;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        // given-1: 테스트에 사용할 사용자, 상품, 장바구니를 미리 생성하고 저장합니다.
        User user = User.builder()
                .email("cartitemuser@example.com")
                .password("password")
                .username("아이템사용자")
                .role(UserRole.USER)
                .build();
        User savedUser = userRepository.save(user);

        Product product = Product.builder()
                .name("테스트용 모니터")
                .price(250000)
                .description("선명한 모니터입니다.")
                .imageUrl(List.of("monitor.jpg"))
                .build();
        savedProduct = productRepository.save(product);

        Cart cart = Cart.builder().user(savedUser).build();
        savedCart = cartRepository.save(cart);
    }

    @Test
    @DisplayName("장바구니 아이템 저장 및 조회 테스트")
    void saveAndFindCartItemTest() {
        // given-2: 저장된 장바구니와 상품으로 장바구니 아이템을 생성합니다.
        CartItem cartItem = CartItem.builder()
                .cart(savedCart)
                .product(savedProduct)
                .quantity(1)
                .build();

        // when: 장바구니 아이템을 저장하고 ID로 다시 조회합니다.
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        CartItem foundCartItem = cartItemRepository.findById(savedCartItem.getId()).orElse(null);

        // then: 저장된 아이템과 조회된 아이템의 정보가 일치해야 합니다.
        assertThat(foundCartItem).isNotNull();
        assertThat(foundCartItem.getQuantity()).isEqualTo(1);
        assertThat(foundCartItem.getProduct().getName()).isEqualTo("테스트용 모니터");
        assertThat(foundCartItem.getCart().getId()).isEqualTo(savedCart.getId());
    }

    @Test
    @DisplayName("장바구니 아이템 수량 변경 테스트")
    void updateQuantityTest() {
        // given: 수량이 1인 장바구니 아이템을 하나 저장합니다.
        CartItem cartItem = CartItem.builder()
                .cart(savedCart)
                .product(savedProduct)
                .quantity(1)
                .build();
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        // when: 저장된 아이템의 수량을 5로 변경합니다.
        // @DataJpaTest는 트랜잭션 내에서 동작하므로, 영속성 컨텍스트가 관리하는 객체의
        // 상태를 변경하면 트랜잭션이 끝날 때 자동으로 DB에 반영(dirty checking)됩니다.
        savedCartItem.updateQuantity(5);
        
        // when-2: DB에 반영된 정보를 확인하기 위해 아이템을 다시 조회합니다.
        CartItem updatedCartItem = cartItemRepository.findById(savedCartItem.getId()).orElseThrow();

        // then: 아이템의 수량이 5로 변경되었는지 확인합니다.
        assertThat(updatedCartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("특정 장바구니의 모든 아이템 삭제 테스트")
    void deleteAllByCart_IdTest() {
        // given: 삭제 대상이 아닌 다른 장바구니와 아이템들을 추가로 생성
        User otherUser = userRepository.save(User.builder()
                .email("other@user.com")
                .password("pw")
                .username("다른유저")
                .role(UserRole.USER) // 👇 [수정] 빠뜨렸던 .role()을 추가합니다.
                .build());
        Cart otherCart = cartRepository.save(Cart.builder().user(otherUser).build());
        cartItemRepository.save(CartItem.builder().cart(otherCart).product(savedProduct).quantity(10).build());

        // given-2: 삭제 대상 장바구니(savedCart)에 아이템들을 추가
        cartItemRepository.save(CartItem.builder().cart(savedCart).product(savedProduct).quantity(1).build());
        cartItemRepository.save(CartItem.builder().cart(savedCart).product(savedProduct).quantity(2).build());
        
        // 초기 상태 검증 (총 3개의 아이템 존재)
        assertThat(cartItemRepository.count()).isEqualTo(3);

        // when: savedCart의 ID를 사용하여 해당 장바구니의 모든 아이템을 삭제
        cartItemRepository.deleteAllByCart_Id(savedCart.getId());

        // then: 전체 아이템 개수는 1개로 줄어들어야 함 (otherCart의 아이템만 남음)
        assertThat(cartItemRepository.count()).isEqualTo(1);
        
        // then-2: 남아있는 아이템은 otherCart의 아이템이 맞는지 확인
        List<CartItem> remainingItems = cartItemRepository.findAll();
        assertThat(remainingItems.get(0).getCart().getId()).isEqualTo(otherCart.getId());
    }
}