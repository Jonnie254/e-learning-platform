package com.jonnie.elearning.cartitem;

import com.jonnie.elearning.cart.Cart;
import com.jonnie.elearning.common.PageResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.openfeign.course.CourseClient;
import com.jonnie.elearning.openfeign.course.CourseResponse;
import com.jonnie.elearning.openfeign.user.UserClient;
import com.jonnie.elearning.repositories.CartItemRepository;
import com.jonnie.elearning.repositories.CartRepository;
import com.jonnie.elearning.repositories.EnrollmentRepository;
import com.jonnie.elearning.utils.CartStatus;
import com.jonnie.elearning.utils.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Collections;
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
    private final EnrollmentRepository enrollmentRepository;
    private final UserClient userClient;

    public void addCartItem(String userId, String courseId) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new BusinessException("User is already enrolled in the course");
        }
        if (userClient.hasRequestedToBeInstructor(userId).orElse(false)) {
            throw new BusinessException("You have already requested to be an instructor, you can't enroll in a course");
        }
        CourseResponse courseResponse = courseClient.getCourseById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found"));
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    newCart.setPaymentMethod(PaymentMethod.PAYPAL);
                    newCart.setReference(generateCartReference());
                    newCart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(newCart);
                });

        if (cartItemRepository.findByCartAndCourseId(cart, courseId).isPresent()) {
            throw new BusinessException("Course is already in the cart");
        }
        cart.setTotalAmount(cart.getTotalAmount().add(courseResponse.getPrice()));
        cartRepository.save(cart);
        CartItem cartItem = cartItemMapper.mapToCartItem(courseResponse, cart, userId);
        cartItemRepository.save(cartItem);
    }

    //method to get all the cart items
    public PageResponse<CartItemResponse> getAllCartItems(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Find the user's active
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("No active or pending cart found"));

        // Fetch cart items for the active cart
        Page<CartItem> cartItems = cartItemRepository.findAllByCart(cart, pageable);
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


    public void removeCartItem(String userId, String courseId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByCartAndCourseId(cart, courseId)
                .orElseThrow(() -> new BusinessException("Course not found in the cart"));

        cartItemRepository.delete(cartItem);
        cart.setTotalAmount(cart.getTotalAmount().subtract(cartItem.getPrice()));
        boolean cartIsEmpty = cartItemRepository.countByCart(cart) == 0;

        if (cartIsEmpty) {
            cartRepository.delete(cart);
        } else {
            cartRepository.save(cart);
        }
    }

    //method to generate the cart reference
    public String generateCartReference() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder reference = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for(int i = 0; i < 6; i++) {
            int randomIndex  = random.nextInt(chars.length());
            reference.append(chars.charAt(randomIndex));
        }
        return reference.toString();
    }
}

