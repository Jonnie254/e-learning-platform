package com.jonnie.elearning.category;

import com.jonnie.elearning.repositories.CategoryRepository;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.CategoryNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public final CategoryMapper categoryMapper;

    //method to create a new category
    public String createCategory(CategoryRequest categoryRequest) {
        Category category = categoryMapper.toCategory(categoryRequest);
        categoryRepository.save(category);
        return category.getCategoryId();
    }

    //method to get all categories
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

    //method to get a category by id
    public CategoryResponse findById(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return categoryMapper.toCategoryResponse(category);
    }

    //method to update a category
    public void updateCategory(String categoryId, @Valid CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        category.setCategoryName(categoryRequest.categoryName());
        categoryRepository.save(category);
    }
}
