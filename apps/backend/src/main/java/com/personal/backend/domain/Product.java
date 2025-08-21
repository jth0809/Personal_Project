package com.personal.backend.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 👇 1. 'javax.persistence'가 아닌 'jakarta.persistence'를 사용해야 합니다.
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_generator")
    @SequenceGenerator(
            name = "product_seq_generator",
            sequenceName = "PRODUCT_SEQ",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
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

    @Column(columnDefinition = "integer default 0")
    private int likeCount = 0;

    @Column(columnDefinition = "integer default 0")
    private int reviewCount = 0;

    @Column(columnDefinition = "NUMBER(19) default 0")
    private long totalRatingScore = 0L;
    
    @Column(columnDefinition = "NUMBER(10, 2) default 0.0")
    private double averageRating = 0.0;
    
    @Column(columnDefinition = "NUMBER(3, 2) default 0.0")
    private double discountRate = 0.0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductTag> productTags = new HashSet<>();

    @Builder
    public Product(String name, String description, int price, List<String> imageUrl, Category category, User user, int stockQuantity, double discountRate) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.user = user;
        this.stockQuantity = stockQuantity;
        this.discountRate = discountRate;
    }

    public void updateDetails(String name, String description, int price, List<String> imageUrl, Category category, int stockQuantity, double discountRate) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.discountRate = discountRate;
    }

    public int getOriginalPrice() {
        return this.price;
    }

    public int getDiscountedPrice() {
        return (int) (this.price * (1 - this.discountRate));
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

    public void increaseLikeCount() { this.likeCount++; }
    
    public void decreaseLikeCount() { this.likeCount = Math.max(0, this.likeCount - 1); }
    
    public void addReview(int rating) {
        this.totalRatingScore += rating;
        this.reviewCount++;
        this.updateAverageRating(); // 평균 평점 재계산
    }

    public void removeReview(int rating) {
        this.totalRatingScore = Math.max(0, this.totalRatingScore - rating);
        this.reviewCount = Math.max(0, this.reviewCount - 1);
        this.updateAverageRating(); // 평균 평점 재계산
    }

    private void updateAverageRating() {
        if (this.reviewCount == 0) {
            this.averageRating = 0.0;
        } else {
            this.averageRating = (double) this.totalRatingScore / this.reviewCount;
        }
    }

}