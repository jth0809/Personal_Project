package com.personal.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_seq_generator")
    @SequenceGenerator(
            name = "cart_seq_generator",
            sequenceName = "CART_SEQ",
            allocationSize = 1
    )
    private Long id;

    // 장바구니의 주인을 나타냅니다. (일대일 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 하나의 장바구니는 여러 개의 장바구니 아이템을 가집니다.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder
    public Cart(User user) {
        this.user = user;
    }

    public void clearItems() {
        this.cartItems.clear();
    }

    public void removeItems(List<Long> productIdsToRemove) {
        if (this.cartItems != null && productIdsToRemove != null) {
            this.cartItems.removeIf(cartItem ->
                productIdsToRemove.contains(cartItem.getProduct().getId())
            );
        }
    }

    public void removeItem(CartItem item){
        this.cartItems.remove(item);
    }
}