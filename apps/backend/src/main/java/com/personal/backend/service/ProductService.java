package com.personal.backend.service;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.ProductDetail;
import com.personal.backend.domain.User;
import com.personal.backend.domain.ShippingInfo;
import com.personal.backend.dto.CategoryDto;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.dto.ShippingInfoDto;
import com.personal.backend.repository.CategoryRepository;
import com.personal.backend.repository.ProductDetailRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;
import com.personal.backend.repository.ShippingInfoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ShippingInfoRepository shippingInfoRepository;

    @Transactional
    public ProductDto.Response createProduct(ProductDto.CreateRequest request, String userEmail) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다. id=" + request.categoryId()));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        Product newProduct = request.toEntity(user);
        
        newProduct.setCategory(category);
        
        Product savedProduct = productRepository.save(newProduct);
        
        return ProductDto.Response.fromEntity(savedProduct);
    }

    @Transactional
    public ProductDto.Response updateProduct(Long productId, ProductDto.UpdateRequest request, String userEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. id=" + productId));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        if (!product.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 상품을 수정할 권한이 없습니다.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        product.updateDetails(
            request.name(),
            request.description(),
            request.price(),
            request.imageUrl(),
            category,
            request.stockQuantity()
        );

        return ProductDto.Response.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(Long productId, String userEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. id=" + productId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        if (!product.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 상품을 삭제할 권한이 없습니다.");
        }

        productRepository.deleteById(productId);
    }

    public Page<ProductDto.Response> findProducts(String keyword, Long categoryId, Pageable pageable) {
        Page<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepository.findByNameContaining(keyword, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(ProductDto.Response::fromEntity);
    }

    public ProductDto.Response findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. id=" + id));

        Optional<ProductDetail> productDetailOpt = productDetailRepository.findById(id);

        return ProductDto.Response.fromEntity(product, productDetailOpt.orElse(null));
    }

    public List<CategoryDto.Response> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryDto.Response::fromEntity)
                .toList();
    }

    public ShippingInfoDto.Response getShippingInfoByProductId(Long productId) {
        ShippingInfo shippingInfo = shippingInfoRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품의 배송 정보를 찾을 수 없습니다: " + productId));
        return ShippingInfoDto.Response.fromEntity(shippingInfo);
    }
}