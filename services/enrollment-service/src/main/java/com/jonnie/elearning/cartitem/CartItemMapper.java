package com.jonnie.elearning.cartitem;

import com.jonnie.elearning.cart.Cart;
import com.jonnie.elearning.openfeign.course.CourseResponse;
import org.springframework.stereotype.Service;

@Service
public class CartItemMapper {
    public CartItem mapToCartItem(CourseResponse courseResponse, Cart cart, String userId) {
        return CartItem.builder()
                .cart(cart)
                .courseId(courseResponse.getCourseId())
                .courseName(courseResponse.getCourseName())
                .price(courseResponse.getPrice())
                .instructorId(courseResponse.getInstructorId())
                .instructorName(courseResponse.getInstructorName())
                .courseImageUrl(courseResponse.getCourseImageUrl())
                .userId(userId)
                .build();
    }

    public CartItemResponse fromCartItem(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getCartItemId())
                .courseId(cartItem.getCourseId())
                .courseName(cartItem.getCourseName())
                .price(cartItem.getPrice())
                .instructorName(cartItem.getInstructorName())
                .courseImageUrl(cartItem.getCourseImageUrl())
                .build();
    }
}
