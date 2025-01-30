package com.jonnie.elearning.cart;


import lombok.*;

import java.math.BigDecimal;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartResponse {
    private String cartId;
    private BigDecimal totalAmount;

}
