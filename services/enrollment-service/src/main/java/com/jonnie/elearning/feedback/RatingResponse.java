package com.jonnie.elearning.feedback;


import lombok.*;

import java.math.BigDecimal;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RatingResponse {
    private BigDecimal progress;
    private boolean isRated;
}
