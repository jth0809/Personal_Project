package com.personal.backend.repository;

import com.personal.backend.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // JPA 관련 컴포넌트만 테스트하도록 설정
@ActiveProfiles("test") // application-test.properties를 사용
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 저장 및 조회 테스트")
    void saveAndFindProductTest() {
        // given: 이런 상품을 만들어서
        Product product = Product.builder()
                .name("테스트용 가방")
                .price(50000)
                .description("설명입니다.")
                .imageUrl("test.jpg")
                .build();

        // when: 데이터베이스에 저장하고, 다시 ID로 조회하면
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        // then: 저장된 상품과 조회된 상품은 동일해야 합니다.
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo(savedProduct.getName());
    }
}
