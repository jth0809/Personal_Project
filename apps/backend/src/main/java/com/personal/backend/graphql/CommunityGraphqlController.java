package com.personal.backend.graphql;

import com.personal.backend.domain.User;
import com.personal.backend.dto.QnaDto;
import com.personal.backend.dto.ReviewDto;
import com.personal.backend.graphql.dto.QnaInput;
import com.personal.backend.graphql.dto.ReviewInput;
import com.personal.backend.service.QnaService;
import com.personal.backend.service.ReviewService;
import com.personal.backend.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

@Controller
@RequiredArgsConstructor
public class CommunityGraphqlController {

    private final ReviewService reviewService;
    private final QnaService qnaService;
    private final UserService userService; // Added UserService

    // --- Query Resolvers ---

    @QueryMapping
    public Page<ReviewDto.Response> reviews(
            @Argument Long productId,
            @Argument Integer page, @Argument Integer size) {
        Pageable pageable = createPageable(page, size, "id", "DESC");
        return reviewService.getReviewsByProductId(productId, pageable);
    }

    @QueryMapping
    public Page<QnaDto.Response> qnas(
            @Argument Long productId,
            @Argument Integer page, @Argument Integer size) {
        Pageable pageable = createPageable(page, size, "id", "DESC");
        return qnaService.getQnaByProductId(productId, pageable);
    }

    // --- Mutation Resolvers ---

    @MutationMapping
    public ReviewDto.Response createReview(@Argument("input") ReviewInput input, @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDto.CreateRequest request = new ReviewDto.CreateRequest(input.productId(), input.rating(), input.comment());
        return reviewService.createReview(userDetails.getUsername(), request);
    }

    @MutationMapping
    public ReviewDto.Response updateReview(@Argument Long reviewId, @Argument("input") ReviewInput input, @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDto.UpdateRequest request = new ReviewDto.UpdateRequest(input.rating(), input.comment());
        return reviewService.updateReview(reviewId, userDetails.getUsername(), request);
    }

    @MutationMapping
    public boolean deleteReview(@Argument Long reviewId, @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return true;
    }

    @MutationMapping
    public QnaDto.Response createQna(@Argument("input") QnaInput input, @AuthenticationPrincipal UserDetails userDetails) {
        QnaDto.CreateRequest request = new QnaDto.CreateRequest(input.productId(), input.question());
        return qnaService.createQuestion(userDetails.getUsername(), request);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public QnaDto.Response answerQna(@Argument Long qnaId, @Argument String answer) {
        QnaDto.AnswerRequest request = new QnaDto.AnswerRequest(answer);
        return qnaService.addAnswer(qnaId, request);
    }

    @MutationMapping
    public boolean deleteQna(@Argument Long qnaId, @AuthenticationPrincipal UserDetails userDetails) {
        qnaService.deleteQna(qnaId, userDetails.getUsername());
        return true;
    }

    // --- Field Resolvers (N+1 해결) ---

    @BatchMapping(typeName = "Review", field = "author")
    public Map<ReviewDto.Response, User> getAuthorForReviews(List<ReviewDto.Response> reviews) {
        Set<String> userEmails = reviews.stream()
                .map(ReviewDto.Response::authorName)
                .collect(Collectors.toSet());

        Map<String, User> usersByEmail = userService.findUsersByEmailIn(userEmails).stream()
                .collect(Collectors.toMap(User::getEmail, Function.identity()));

        return reviews.stream()
                .collect(Collectors.toMap(Function.identity(), review -> usersByEmail.get(review.authorName())));
    }

    @BatchMapping(typeName = "Qna", field = "author")
    public Map<QnaDto.Response, User> getAuthorForQnas(List<QnaDto.Response> qnas) {
        Set<String> userEmails = qnas.stream()
                .map(QnaDto.Response::authorName)
                .collect(Collectors.toSet());

        Map<String, User> usersByEmail = userService.findUsersByEmailIn(userEmails).stream()
                .collect(Collectors.toMap(User::getEmail, Function.identity()));

        return qnas.stream()
                .collect(Collectors.toMap(Function.identity(), qna -> usersByEmail.get(qna.authorName())));
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

