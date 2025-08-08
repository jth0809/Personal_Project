package com.personal.backend.repository;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest // JPA 관련 컴포넌트만 테스트하도록 설정
@ActiveProfiles("test") // application-test.properties를 사용
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategoryShoes;
    private Category savedCategoryClothes;

    @BeforeEach
    void setUp() {
        // given: 테스트에 사용할 카테고리들을 미리 생성
        savedCategoryShoes = categoryRepository.save(new Category("신발"));
        savedCategoryClothes = categoryRepository.save(new Category("의류"));
    }

    @Test
    @DisplayName("product save and read test")
    void saveAndFindProductTest() {
        // given: 이런 상품을 만들어서
        Product product = Product.builder()
                .name("테스트용 가방")
                .price(50000)
                .description("설명입니다.")
                .imageUrl(List.of("test.jpg"))
                .build();

        // when: 데이터베이스에 저장하고, 다시 ID로 조회하면
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        // then: 저장된 상품과 조회된 상품은 동일해야 합니다.
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo(savedProduct.getName());
    }

    // 👇 추가된 테스트 메소드
    @Test
    @DisplayName("상품 이름 포함 검색(findByNameContaining) 테스트")
    void findByNameContainingTest() {
        Pageable pageable = PageRequest.of(0, 10);
        // given: 다양한 상품을 저장
        productRepository.save(Product.builder().name("나이키 에어포스").price(130000).build());
        productRepository.save(Product.builder().name("나이키 조던").price(170000).build());
        productRepository.save(Product.builder().name("아디다스 슈퍼스타").price(110000).build());
        
        // when: "나이키" 라는 키워드로 상품을 검색
        Page<Product> nikeProducts = productRepository.findByNameContaining("나이키",pageable);
        Page<Product> adidasProducts = productRepository.findByNameContaining("아디다스",pageable);
        Page<Product> nonExistentProducts = productRepository.findByNameContaining("없는상품",pageable);

        // then: "나이키"가 포함된 상품은 2개, "아디다스"는 1개, "없는상품"은 0개여야 함
        assertThat(nikeProducts).hasSize(2);
        assertThat(adidasProducts).hasSize(1);
        assertThat(nonExistentProducts).isEmpty();
    }
    
    // 👇 추가된 테스트 메소드
    @Test
    @DisplayName("카테고리 ID로 상품 조회(findByCategoryId) 테스트")
    void findByCategoryIdTest() {
        Pageable pageable = PageRequest.of(0, 10);
        // given: 각 카테고리별로 상품을 저장
        productRepository.save(Product.builder().name("나이키 에어포스").price(130000).category(savedCategoryShoes).build());
        productRepository.save(Product.builder().name("리바이스 청바지").price(90000).category(savedCategoryClothes).build());
        productRepository.save(Product.builder().name("아디다스 슈퍼스타").price(110000).category(savedCategoryShoes).build());

        // when: '신발' 카테고리 ID로 상품을 조회
        Page<Product> shoeProducts = productRepository.findByCategoryId(savedCategoryShoes.getId(),pageable);
        Page<Product> clothesProducts = productRepository.findByCategoryId(savedCategoryClothes.getId(),pageable);

        // then: '신발' 카테고리 상품은 2개, '의류' 카테고리 상품은 1개여야 함
        assertThat(shoeProducts).hasSize(2);
        assertThat(shoeProducts).extracting(Product::getName).contains("나이키 에어포스", "아디다스 슈퍼스타");
        assertThat(clothesProducts).hasSize(1);
    }
}
