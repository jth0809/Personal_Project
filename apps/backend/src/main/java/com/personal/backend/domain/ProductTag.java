package com.personal.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_tag_seq_generator")
    @SequenceGenerator(name = "product_tag_seq_generator", sequenceName = "PRODUCT_TAG_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public ProductTag(Product product, Tag tag) {
        this.product = product;
        this.tag = tag;
    }
}
