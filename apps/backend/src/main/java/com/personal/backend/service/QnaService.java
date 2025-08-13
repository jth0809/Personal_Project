package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.Qna;
import com.personal.backend.domain.User;
import com.personal.backend.dto.QnaDto;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.QnaRepository;
import com.personal.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<QnaDto.Response> getQnaByProductId(Long productId, Pageable pageable) {
        return qnaRepository.findByProductId(productId, pageable)
                .map(QnaDto.Response::fromEntity);
    }

    public QnaDto.Response createQuestion(String userEmail, QnaDto.CreateRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        Qna qna = Qna.builder()
                .user(user)
                .product(product)
                .question(request.question())
                .build();

        Qna savedQna = qnaRepository.save(qna);
        return QnaDto.Response.fromEntity(savedQna);
    }

    public QnaDto.Response addAnswer(Long qnaId, QnaDto.AnswerRequest request) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new EntityNotFoundException("Q&A를 찾을 수 없습니다."));

        qna.addAnswer(request.answer());
        return QnaDto.Response.fromEntity(qna);
    }

    public void deleteQna(Long qnaId, String userEmail) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new EntityNotFoundException("Q&A를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!qna.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Q&A를 삭제할 권한이 없습니다.");
        }

        qnaRepository.delete(qna);
    }
}
