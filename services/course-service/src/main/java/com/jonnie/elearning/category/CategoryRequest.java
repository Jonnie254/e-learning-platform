package com.jonnie.elearning.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        String categotyId,
        @NotNull(message = "Category name is required")
        @NotEmpty(message = "Category name is required")
        String categoryName
) {
}
