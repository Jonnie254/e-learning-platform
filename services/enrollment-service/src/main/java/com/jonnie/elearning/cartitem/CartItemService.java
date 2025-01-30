package com.jonnie.elearning.cartitem;

import com.jonnie.elearning.cart.Cart;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.openfeign.course.CourseResponse;
import com.jonnie.elearning.repositories.CartItemRepository;
import com.jonnie.elearning.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CourseClient courseClient;
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;

    // method to add a course to the cart
    public void addCartItem(String userId, String courseId) {
        // Get the course details
        CourseResponse courseResponse = courseClient.getCourseById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found"));
        // Fetch the user's cart, or create one if it doesn't exist
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });
        // Check if the course is already in the cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndCourseId(cart, courseId);
        if (existingCartItem.isPresent()) {
            throw new BusinessException("Course is already in the cart");
        }
        // Update the total amount of the cart
        cart.setTotalAmount(cart.getTotalAmount().add(courseResponse.getPrice()));
        cartRepository.save(cart);
        // Create and save the new cart item
        CartItem cartItem = cartItemMapper.mapToCartItem(courseResponse, cart, userId);
        cartItemRepository.save(cartItem);
    }

    //method to get all the cart items
    public PageResponse<CartItemResponse> getAllCartItems(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CartItem> cartItems = cartItemRepository.findAllByUserId(userId, pageable);
        List<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(cartItemMapper::fromCartItem)
                .toList();
        return new PageResponse<>(
                cartItemResponses,
                cartItems.getNumber(),
                cartItems.getSize(),
                cartItems.getTotalElements(),
                cartItems.getTotalPages(),
                cartItems.isFirst(),
                cartItems.isLast()
        );
    }

    //method to remove a course from the cart
    public void removeCartItem(String userId, String courseId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Cart not found"));
        CartItem cartItem = cartItemRepository.findByCartAndCourseId(cart, courseId)
                .orElseThrow(() -> new BusinessException("Course not found in the cart"));
        cartItemRepository.delete(cartItem);
        cart.setTotalAmount(cart.getTotalAmount().subtract(cartItem.getPrice()));
        cartRepository.save(cart);
    }
}

