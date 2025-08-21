package com.personal.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_option_seq_generator")
    @SequenceGenerator(
            name = "product_option_seq_generator",
            sequenceName = "PRODUCT_OPTION_SEQ",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String optionGroupName;
    
    private String optionName;
    
    private int additionalPrice;
    
    private int stockQuantity;

    @Builder
    public ProductOption(Product product, String optionGroupName, String optionName, int additionalPrice, int stockQuantity) {
        this.product = product;
        this.optionGroupName = optionGroupName;
        this.optionName = optionName;
        this.additionalPrice = additionalPrice;
        this.stockQuantity = stockQuantity;
    }
}