package com.personal.backend.domain;

import java.util.ArrayList;
import java.util.List;

// 👇 1. 'javax.persistence'가 아닌 'jakarta.persistence'를 사용해야 합니다.
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products") // 테이블 이름은 보통 복수형을 사용합니다.
@Getter
// 👇 2. 무분별한 Setter를 막고, 필요한 생성자만 노출하여 객체의 일관성을 유지합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    // 👇 3. ID 생성 전략을 DB에 위임하는 IDENTITY 방식이 더 간단하고 일반적입니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👇 4. 프론트엔드 요구사항에 맞는 실제 컬럼들을 추가합니다.
    @Column(nullable = false) // NOT NULL 제약조건
    private String name;

    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockQuantity;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_image_urls",
        joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrl = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 👇 5. Builder 패턴을 사용하여 객체 생성을 더 명확하고 유연하게 만듭니다.
    @Builder
    public Product(String name, String description, int price, List<String> imageUrl, Category category, User user, int stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.user = user;
        this.stockQuantity = stockQuantity;
    }

    // 👇 6. Setter 대신, 의미가 명확한 비즈니스 메소드를 통해 데이터를 변경합니다.
    public void updateDetails(String name, String description, int price, List<String> imageUrl, Category category, int stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    

    public void deleteImageUrl(String imageName){
        this.imageUrl.remove(imageName);
    }

    /**
     * 재고를 감소시킵니다.
     * @param quantity 감소시킬 수량
     */
    public void decreaseStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            // 재고가 부족하면 예외를 발생시켜 주문 진행을 막습니다.
            throw new IllegalStateException("재고가 부족합니다. (상품명: " + this.getName() + ")");
        }
        this.stockQuantity = restStock;
    }

    /**
     * 재고를 증가시킵니다. (주문 취소 시 사용)
     * @param quantity 증가시킬 수량
     */
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

}
