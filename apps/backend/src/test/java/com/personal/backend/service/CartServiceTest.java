package com.personal.backend.service;

import com.personal.backend.domain.*;
import com.personal.backend.dto.CartDto;
import com.personal.backend.repository.CartRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartRepository cartRepository;

    private User dummyUser;
    private Product dummyProduct;
    private Cart dummyCart;

    

    @BeforeEach
    void setUp() {
        dummyUser = User.builder().email("test@user.com").build();
        dummyProduct = Product.builder().name("테스트 상품").price(10000).imageUrl(List.of("default-image.jpg")).build();
        try { // ID 설정
            var productIdField = Product.class.getDeclaredField("id");
            productIdField.setAccessible(true);
            productIdField.set(dummyProduct, 100L);
        } catch (Exception e) { e.printStackTrace(); }

        // CartItem 리스트를 직접 수정할 수 있도록 ArrayList로 생성
        dummyCart = Cart.builder().user(dummyUser).build();
        try {
            var itemsField = Cart.class.getDeclaredField("cartItems");
            itemsField.setAccessible(true);
            itemsField.set(dummyCart, new ArrayList<>());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("장바구니에 새 상품 추가 성공")
    void addItem_AddNewItem_Success() {
        // given
        String userEmail = "test@user.com";
        CartDto.AddItemRequest request = new CartDto.AddItemRequest(100L, 2);

        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(cartRepository.findByUser(dummyUser)).thenReturn(Optional.of(dummyCart)); // 비어있는 장바구니 반환
        when(productRepository.findById(100L)).thenReturn(Optional.of(dummyProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(dummyCart);

        // when
        cartService.addItem(userEmail, request);

        // then
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();
        assertThat(savedCart.getCartItems()).hasSize(1);
        assertThat(savedCart.getCartItems().get(0).getProduct().getName()).isEqualTo("테스트 상품");
        assertThat(savedCart.getCartItems().get(0).getQuantity()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("장바구니에 기존 상품 수량 추가 성공")
    void addItem_IncreaseQuantity_Success() {
        // given
        String userEmail = "test@user.com";
        CartDto.AddItemRequest request = new CartDto.AddItemRequest(100L, 2); // 2개 더 추가

        // 이미 상품이 1개 담겨있는 장바구니 아이템과 장바구니를 설정
        CartItem existingItem = CartItem.builder().cart(dummyCart).product(dummyProduct).quantity(1).build();
        dummyCart.getCartItems().add(existingItem);
        
        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(cartRepository.findByUser(dummyUser)).thenReturn(Optional.of(dummyCart)); // 아이템이 담긴 장바구니 반환
        when(productRepository.findById(100L)).thenReturn(Optional.of(dummyProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(dummyCart);

        // when
        cartService.addItem(userEmail, request);

        // then
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();
        assertThat(savedCart.getCartItems()).hasSize(1); // 아이템 종류는 그대로 1개
        assertThat(savedCart.getCartItems().get(0).getQuantity()).isEqualTo(3); // 수량은 1 + 2 = 3
    }
    
    @Test
    @DisplayName("장바구니 상품 삭제 성공")
    void removeItem_Success() {
        // given
        String userEmail = "test@user.com";
        Long cartItemIdToRemove = 1L;

        CartItem itemToRemove = CartItem.builder().cart(dummyCart).product(dummyProduct).quantity(1).build();
        try { // ID 설정
            var cartItemIdField = CartItem.class.getDeclaredField("id");
            cartItemIdField.setAccessible(true);
            cartItemIdField.set(itemToRemove, cartItemIdToRemove);
        } catch (Exception e) { e.printStackTrace(); }
        dummyCart.getCartItems().add(itemToRemove);
        
        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(cartRepository.findByUser(dummyUser)).thenReturn(Optional.of(dummyCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(dummyCart);
        
        // when
        cartService.removeItemFromCart(userEmail, cartItemIdToRemove);

        // then
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository, times(1)).save(cartCaptor.capture());
        
        Cart savedCart = cartCaptor.getValue();
        assertThat(savedCart.getCartItems()).isEmpty(); // 장바구니가 비어있는지 확인
    }
    
    @Test
    @DisplayName("장바구니 상품 삭제 실패 - 아이템이 없는 경우")
    void removeItem_Fail_ItemNotFound() {
        // given
        String userEmail = "test@user.com";
        Long cartItemIdToRemove = 99L; // 존재하지 않는 아이템 ID

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(cartRepository.findByUser(dummyUser)).thenReturn(Optional.of(dummyCart)); // 비어있는 장바구니

        // when & then
        assertThrows(IllegalArgumentException.class, () -> cartService.removeItemFromCart(userEmail, cartItemIdToRemove));
    }
}