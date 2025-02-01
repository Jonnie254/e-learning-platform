package com.jonnie.elearning.cart;


import com.jonnie.elearning.cartitem.CartItemResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartResponse {
    private String cartId;
    private BigDecimal totalAmount;
    private String reference;
    List<CartItemResponse> cartItems;

}
