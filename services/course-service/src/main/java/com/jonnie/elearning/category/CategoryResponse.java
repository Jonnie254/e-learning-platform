package com.jonnie.elearning.category;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private String categoryId;
    private String categoryName;
}
