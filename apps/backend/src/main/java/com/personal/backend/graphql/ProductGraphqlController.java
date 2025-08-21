package com.personal.backend.graphql;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.Tag;
import com.personal.backend.domain.User;
import com.personal.backend.dto.CategoryDto;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.graphql.dto.ProductInput;
import com.personal.backend.service.CategoryService;
import com.personal.backend.service.LikeService;
import com.personal.backend.service.ProductService;
import com.personal.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProductGraphqlController {

    private final ProductService productService;
    private final LikeService likeService;
    private final TagService tagService;
    private final CategoryService categoryService;


    // --- Query Resolvers ---

    @QueryMapping
    public Page<ProductDto.Response> products(
            @Argument String keyword, @Argument Long categoryId,
            @Argument Integer page, @Argument Integer size,
            @Argument String sortBy, @Argument String sortOrder,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = (userDetails != null) ? userDetails.getUsername() : null;
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        return productService.findProducts(keyword, categoryId, pageable, userEmail);
    }

    @QueryMapping
    public ProductDto.Response product(@Argument Long id, @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = (userDetails != null) ? userDetails.getUsername() : null;
        return productService.findProductById(id, userEmail);
    }

    @QueryMapping
    public Page<ProductDto.Response> myLikes(
            @Argument Integer page, @Argument Integer size,
            @Argument String sortBy, @Argument String sortOrder,
            @AuthenticationPrincipal UserDetails userDetails) {
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        return likeService.getLikedProducts(userDetails.getUsername(), pageable);
    }

    @QueryMapping
    public List<Tag> tags() {
        return tagService.findAllTags();
    }

    @QueryMapping
    public List<CategoryDto.Response> categories() {
        return productService.findAllCategories();
    }

    // --- Mutation Resolvers ---

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto.Response createProduct(@Argument("input") ProductInput input, @AuthenticationPrincipal UserDetails userDetails) {
        ProductDto.CreateRequest request = new ProductDto.CreateRequest(
                input.name(), input.description(), input.price(), input.imageUrl(),
                input.categoryId(), input.stockQuantity(), input.discountRate());
        return productService.createProduct(request, userDetails.getUsername());
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto.Response updateProduct(@Argument Long id, @Argument("input") ProductInput input, @AuthenticationPrincipal UserDetails userDetails) {
        ProductDto.UpdateRequest request = new ProductDto.UpdateRequest(
                input.name(), input.description(), input.price(), input.imageUrl(),
                input.categoryId(), input.stockQuantity(), input.detailContent(), input.discountRate());
        return productService.updateProduct(id, request, userDetails.getUsername());
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteProduct(@Argument Long id, @AuthenticationPrincipal UserDetails userDetails) {
        productService.deleteProduct(id, userDetails.getUsername());
        return true;
    }

    @MutationMapping
    public ProductDto.Response addLike(@Argument Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        likeService.addLike(userDetails.getUsername(), productId);
        return productService.findProductById(productId, userDetails.getUsername());
    }

    @MutationMapping
    public ProductDto.Response removeLike(@Argument Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        likeService.removeLike(userDetails.getUsername(), productId);
        return productService.findProductById(productId, userDetails.getUsername());
    }

    @MutationMapping
    public Tag createTag(@Argument String name) {
        return tagService.findOrCreate(name);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category createCategory(@Argument String name) {
        return categoryService.createCategory(name);
    }

    // --- Field Resolvers (N+1 해결) ---

    @BatchMapping(typeName = "Product", field = "category")
    public Map<ProductDto.Response, Category> getCategory(List<ProductDto.Response> products) {
        Set<String> categoryNames = products.stream()
                .map(ProductDto.Response::categoryName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Category> categoriesByName =
                productService.findCategoriesByNameIn(categoryNames).stream()
                        .collect(Collectors.toMap(Category::getName, Function.identity()));

        return products.stream()
                .filter(p -> p.categoryName() != null) // null인 요소 필터링
                .collect(Collectors.toMap(
                    Function.identity(),
                    p -> categoriesByName.getOrDefault(p.categoryName(), null) // getOrDefault로 null 값 안전하게 처리
                ));
    }

    @BatchMapping(typeName = "Product", field = "user")
    public Map<ProductDto.Response, User> getUser(List<ProductDto.Response> products) {
        Set<Long> productIds = products.stream()
                .map(ProductDto.Response::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        Map<Long, User> userByProductId = productService.findProductsByIdIn(productIds).stream()
                .filter(p -> p.getUser() != null) // null인 user를 가진 Product 필터링
                .collect(Collectors.toMap(Product::getId, Product::getUser));

        return products.stream()
                .filter(p -> p.id() != null) // null인 id를 가진 ProductDto.Response 필터링
                .collect(Collectors.toMap(
                    Function.identity(),
                    p -> userByProductId.getOrDefault(p.id(), null) // getOrDefault로 null 값 안전하게 처리
                ));
    }

    // --- Helper Methods ---

    private Pageable createPageable(Integer page, Integer size, String sortBy, String sortOrder) {
        int pageNumber = page != null && page >= 0 ? page : 0;
        int pageSize = size != null && size > 0 ? size : 10;
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = sortBy != null && !sortBy.isBlank() ? sortBy : "id";
        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
    }
}