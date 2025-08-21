package com.personal.backend.service;

import com.personal.backend.domain.Category;
import com.personal.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(String name) {
        // You might want to add validation here, e.g., check if category name already exists
        return categoryRepository.save(new Category(name));
    }
}
