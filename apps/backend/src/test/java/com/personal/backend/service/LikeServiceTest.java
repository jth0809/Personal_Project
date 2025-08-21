package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.ProductLike;
import com.personal.backend.domain.User;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.repository.ProductLikeRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private ProductLikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    private User dummyUser;
    private Product dummyProduct;

    @BeforeEach
    void setUp() {
        dummyUser = User.builder().email("test@user.com").build();
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyUser, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dummyProduct = Product.builder().name("Test Product").build();
        try {
            var idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyProduct, 100L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("찜 추가 성공")
    void addLike_Success() {
        // given
        String userEmail = "test@user.com";
        Long productId = 100L;

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));
        when(likeRepository.existsByUser_IdAndProduct_Id(dummyUser.getId(), productId)).thenReturn(false);

        // when
        likeService.addLike(userEmail, productId);

        // then
        verify(likeRepository, times(1)).save(any(ProductLike.class));
        assertThat(dummyProduct.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("찜 추가 - 이미 찜한 경우")
    void addLike_AlreadyExists() {
        // given
        String userEmail = "test@user.com";
        Long productId = 100L;

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));
        when(likeRepository.existsByUser_IdAndProduct_Id(dummyUser.getId(), productId)).thenReturn(true);

        // when
        likeService.addLike(userEmail, productId);

        // then
        verify(likeRepository, never()).save(any(ProductLike.class));
        assertThat(dummyProduct.getLikeCount()).isEqualTo(0); // likeCount should not be increased
    }

    @Test
    @DisplayName("찜 삭제 성공")
    void removeLike_Success() {
        // given
        String userEmail = "test@user.com";
        Long productId = 100L;
        dummyProduct.increaseLikeCount(); // likeCount is 1 initially
        ProductLike dummyLike = ProductLike.builder().user(dummyUser).product(dummyProduct).build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(likeRepository.findByUser_IdAndProduct_Id(dummyUser.getId(), productId)).thenReturn(Optional.of(dummyLike));

        // when
        likeService.removeLike(userEmail, productId);

        // then
        verify(likeRepository, times(1)).delete(any(ProductLike.class));
        assertThat(dummyProduct.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("찜 삭제 실패 - 찜한 내역 없음")
    void removeLike_Fail_NotFound() {
        // given
        String userEmail = "test@user.com";
        Long productId = 100L;

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(likeRepository.findByUser_IdAndProduct_Id(dummyUser.getId(), productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> likeService.removeLike(userEmail, productId));
        verify(likeRepository, never()).delete(any(ProductLike.class));
        assertThat(dummyProduct.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("찜 목록 조회 성공")
    void getLikedProducts_Success() {
        // given
        String userEmail = "test@user.com";
        Pageable pageable = PageRequest.of(0, 10);
        ProductLike dummyLike = ProductLike.builder().user(dummyUser).product(dummyProduct).build();
        List<ProductLike> likeList = List.of(dummyLike);
        Page<ProductLike> likePage = new PageImpl<>(likeList, pageable, likeList.size());

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(likeRepository.findByUser_Id(dummyUser.getId(), pageable)).thenReturn(likePage);

        // when
        Page<ProductDto.Response> result = likeService.getLikedProducts(userEmail, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Test Product");
    }
}
