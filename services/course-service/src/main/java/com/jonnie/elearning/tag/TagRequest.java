package com.jonnie.elearning.tag;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record TagRequest(
        String tagId,
        @NotNull(message = "Tag name is required")
        @NotEmpty(message = "Tag name is required")
        String tagName
) {
}
