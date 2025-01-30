package com.jonnie.elearning.cartitem;


import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse{
    private String cartItemId;
    private String courseId;
    private String courseName;
    private String instructorName;
    private String courseImageUrl;
   private BigDecimal price;
}
