package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.ProductLike;
import com.personal.backend.domain.User;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.repository.ProductLikeRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨에 @Transactional을 붙여 모든 public 메소드가 트랜잭션 내에서 실행되도록 함
public class LikeService {

    private final ProductLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addLike(String userEmail, Long productId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 이미 찜한 상품인지 확인 후, 찜하지 않았을 때만 로직 실행
        if (!likeRepository.existsByUser_IdAndProduct_Id(user.getId(), productId)) {
            ProductLike newLike = ProductLike.builder()
                    .user(user)
                    .product(product)
                    .build();
            likeRepository.save(newLike);

            // Product 엔티티의 likeCount 1 증가
            product.increaseLikeCount();
            // @Transactional에 의해 메소드 종료 시 변경된 product가 자동으로 DB에 반영됨 (dirty checking)
        }
    }

    public void removeLike(String userEmail, Long productId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 찜 정보를 찾아서 삭제
        ProductLike like = likeRepository.findByUser_IdAndProduct_Id(user.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException("찜한 정보를 찾을 수 없습니다."));

        likeRepository.delete(like);

        // Product 엔티티의 likeCount 1 감소
        // like.getProduct()로 프록시 객체가 아닌 실제 Product를 가져와서 업데이트
        Product product = like.getProduct();
        product.decreaseLikeCount();
    }

    @Transactional(readOnly = true) // 조회만 하므로 readOnly=true로 성능 최적화
    public Page<ProductDto.Response> getLikedProducts(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Page<ProductLike> likedItems = likeRepository.findByUser_Id(user.getId(), pageable);
        
        // 찜 목록 조회 시에는 isLiked가 항상 true
        return likedItems.map(like -> ProductDto.Response.fromEntity(like.getProduct()));
    }
}