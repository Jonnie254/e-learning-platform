package com.jonnie.elearning.progress;


import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressResponse {
    private BigDecimal progress;
}
