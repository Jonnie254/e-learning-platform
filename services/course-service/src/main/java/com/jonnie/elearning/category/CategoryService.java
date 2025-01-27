package com.jonnie.elearning.category;

import com.jonnie.elearning.Repositories.CategoryRepository;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.CategoryNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.jonnie.elearning.category.CategoryRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public final CategoryMapper categoryMapper;
    public String createCategory(CategoryRequest categoryRequest) {
        Category category = categoryMapper.toCategory(categoryRequest);
        categoryRepository.save(category);
        return category.getCategoryId();
    }

    public PageResponse<CategoryResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<CategoryResponse> categoryResponses = categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
        return new PageResponse<>(
                categoryResponses,
                categories.getNumber(),
                categories.getSize(),
                categories.getTotalElements(),
                categories.getTotalPages(),
                categories.isLast(),
                categories.isFirst()
        );
    }

    public CategoryResponse findById(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return categoryMapper.toCategoryResponse(category);
    }
}
