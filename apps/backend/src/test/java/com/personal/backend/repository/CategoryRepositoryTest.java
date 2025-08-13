package com.personal.backend.repository;

import com.personal.backend.domain.Category;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("카테고리 저장 및 조회 테스트")
    void saveAndFindCategoryTest() {
        // given: 새로운 카테고리를 생성합니다.
        Category category = new Category("전자기기");

        // when: 카테고리를 저장하고 ID로 다시 조회합니다.
        Category savedCategory = categoryRepository.save(category);
        Category foundCategory = categoryRepository.findById(savedCategory.getId()).orElse(null);

        // then: 저장된 카테고리와 조회된 카테고리의 이름이 일치해야 합니다.
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getName()).isEqualTo("전자기기");
    }

    @Test
    @DisplayName("중복된 이름의 카테고리 저장 시 예외 발생 테스트")
    void saveDuplicateCategoryNameTest() {
        // given: 첫 번째 카테고리를 생성하고 저장합니다.
        Category category1 = new Category("의류");
        categoryRepository.save(category1);

        // when: 동일한 이름으로 두 번째 카테고리를 저장합니다.
        Category category2 = new Category("의류");
        categoryRepository.save(category2);
        
        // then: flush를 호출하여 DB 제약 조건 위반이 발생하도록 유도하고,
        //      DataIntegrityViolationException이 발생하는지 확인합니다.
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.flush();
        });
    }

    // 👇 추가된 테스트 메소드
    @Test
    @DisplayName("이름으로 카테고리 조회 테스트")
    void findByNameTest() {
        // given: 특정 이름의 카테고리를 저장
        String categoryName = "주방용품";
        categoryRepository.save(new Category(categoryName));

        // when: 해당 이름으로 카테고리를 조회
        Optional<Category> foundCategoryOptional = categoryRepository.findByName(categoryName);

        // then: 조회된 카테고리가 존재하며 이름이 일치하는지 확인
        assertThat(foundCategoryOptional).isPresent();
        assertThat(foundCategoryOptional.get().getName()).isEqualTo(categoryName);
    }

    // 👇 추가된 테스트 메소드
    @Test
    @DisplayName("존재하지 않는 이름으로 카테고리 조회 시 빈 결과 반환 테스트")
    void findByNameWhenNotExistsTest() {
        // given: 데이터베이스에 존재하지 않는 카테고리 이름
        String nonExistentName = "없는 카테고리";

        // when: 존재하지 않는 이름으로 카테고리를 조회
        Optional<Category> foundCategoryOptional = categoryRepository.findByName(nonExistentName);

        // then: 조회 결과는 비어 있어야 함
        assertThat(foundCategoryOptional).isEmpty();
    }
}