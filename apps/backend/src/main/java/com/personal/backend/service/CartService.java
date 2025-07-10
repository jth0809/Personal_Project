package com.personal.backend.service;

import com.personal.backend.domain.Cart;
import com.personal.backend.domain.CartItem;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;
import com.personal.backend.dto.CartDto;
import com.personal.backend.repository.CartItemRepository;
import com.personal.backend.repository.CartRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    
    @Transactional(readOnly = true)
    public CartDto.CartResponse getCartForUser(String userEmail) {

        Cart cart = readCart(userEmail);

        int totalPrice = cart.getCartItems().stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        return new CartDto.CartResponse(
                cart.getCartItems().stream()
                        .map(item -> new CartDto.CartItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getProduct().getImageUrl(),
                                item.getQuantity()
                        )).collect(Collectors.toList()),
                totalPrice
        );
    }

    public CartDto.CartResponse addItem(String userEmail, CartDto.AddItemRequest request) {
        // 상품 ID로 상품을 조회하고, 사용자의 장바구니에 아이템을 추가하는 로직
        Cart cart = readCart(userEmail);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst()
                .ifPresentOrElse(
                        // 이미 있으면 요청된 수량만큼 수량을 증가시킵니다.
                        item -> item.updateQuantity(item.getQuantity() + request.quantity()),
                        // 없으면 장바구니에 새로운 아이템으로 추가합니다.
                        () -> {
                            CartItem newCartItem = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(request.quantity())
                                    .build();
                            cart.getCartItems().add(newCartItem);
                        }
                );
        return convertCartToDto(cartRepository.save(cart));
    }

    public CartDto.CartResponse removeItemFromCart(String userEmail, Long cartItemId) {
        Cart cart = readCart(userEmail);

        // 삭제하려는 아이템이 정말 이 사용자의 장바구니에 속하는지 확인합니다.
        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 아이템이 없습니다."));

        // Cart의 리스트에서 아이템을 제거합니다.
        // orphanRemoval=true 설정 덕분에, 리스트에서 제거되면 DB에서도 자동으로 DELETE 됩니다.
        cart.getCartItems().remove(cartItemToRemove);

        // 변경된 장바구니 상태를 저장합니다.
        return convertCartToDto(cartRepository.save(cart));
    }

    private Cart readCart(String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    // 만약 사용자의 장바구니가 없다면(예: 신규 가입 직후), 새로 생성해줍니다.
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    private CartDto.CartResponse convertCartToDto(Cart cart) {
        int totalPrice = cart.getCartItems().stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        return new CartDto.CartResponse(
                cart.getCartItems().stream()
                        .map(item -> new CartDto.CartItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getProduct().getImageUrl(),
                                item.getQuantity()
                        )).collect(Collectors.toList()),
                totalPrice
        );
    }
}
