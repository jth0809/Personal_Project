package com.personal.backend.service;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.repository.CategoryRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (생성자 주입)
@Transactional(readOnly = true) // 기본적으로 모든 메소드는 읽기 전용 트랜잭션으로 설정
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 상품을 생성합니다.
     */
    @Transactional
    public ProductDto.Response createProduct(ProductDto.CreateRequest request, String userEmail) {
        // 1. 요청 DTO에서 받은 categoryId로 Category 엔티티를 조회합니다.
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다. id=" + request.categoryId()));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        // 2. DTO를 Product 엔티티로 변환합니다.
        
        Product newProduct = request.toEntity(user);
        
        // 3. 조회한 Category 엔티티를 Product에 설정합니다.
        newProduct.setCategory(category);
        
        // 4. 완성된 Product 엔티티를 저장합니다.
        Product savedProduct = productRepository.save(newProduct);
        
        // 5. 저장된 최종 엔티티를 다시 DTO로 변환하여 반환합니다.
        return ProductDto.Response.fromEntity(savedProduct);
    }

    /**
     * 상품 정보를 수정합니다.
     */
    @Transactional // 데이터를 변경하므로 쓰기 트랜잭션 설정
    public ProductDto.Response updateProduct(Long productId, ProductDto.UpdateRequest request, String userEmail) {
        // 1. 수정할 상품을 DB에서 조회합니다. 없으면 예외를 발생시킵니다.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. id=" + productId));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        if (!product.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 상품을 수정할 권한이 없습니다.");
        }

        // 2. 엔티티의 비즈니스 메소드를 호출하여 데이터를 업데이트합니다.
        // TODO: Category 변경 로직 추가 필요
        product.updateDetails(
            request.name(),
            request.description(),
            request.price(),
            request.imageUrl()
        );

        // @Transactional 어노테이션 덕분에, 메소드가 끝나면 변경된 내용이
        // 자동으로 DB에 반영(update 쿼리 실행)됩니다.
        return ProductDto.Response.fromEntity(product);
    }

    /**
     * 상품을 삭제합니다.
     */
    @Transactional // 데이터를 변경하므로 쓰기 트랜잭션 설정
    public void deleteProduct(Long productId, String userEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. id=" + productId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        if (!product.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 상품을 삭제할 권한이 없습니다.");
        }

        // 2. Repository를 통해 DB에서 삭제합니다.
        productRepository.deleteById(productId);
    }

    /**
     * 👇 핵심 수정: 기존 findAllProducts를 더 유연한 findProducts로 변경합니다.
     * 카테고리 ID가 있으면 필터링하고, 없으면 모든 상품을 조회합니다.
     */
    public List<ProductDto.Response> findProducts(Long categoryId) {
        List<Product> products;
        if (categoryId != null) {
            // 카테고리 ID가 주어진 경우, 해당 카테고리의 상품만 조회
            products = productRepository.findByCategoryId(categoryId);
        } else {
            // 카테고리 ID가 없는 경우, 모든 상품 조회
            products = productRepository.findAll();
        }

        // 조회된 Product 엔티티 리스트를 Response DTO 리스트로 변환하여 반환
        return products.stream()
                .map(ProductDto.Response::fromEntity)
                .toList();
    }

    // ID로 상품 단일 조회
    public ProductDto.Response findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. id=" + id));
        return ProductDto.Response.fromEntity(product);
    }
}
