package com.personal.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetail {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Product product;

    @Lob
    @Column(nullable = false)
    private String content; // 상품 상세 정보 (HTML, Markdown 등)

    @Builder
    public ProductDetail(Product product, String content) {
        this.product = product;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}