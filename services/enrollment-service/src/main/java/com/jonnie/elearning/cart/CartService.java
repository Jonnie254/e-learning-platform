package com.jonnie.elearning.cart;

import com.jonnie.elearning.cartitem.CartItem;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.openfeign.payment.PaymentClient;
import com.jonnie.elearning.openfeign.payment.PaymentRequest;
import com.jonnie.elearning.openfeign.user.UserClient;
import com.jonnie.elearning.openfeign.user.UserResponse;
import com.jonnie.elearning.repositories.CartRepository;
import com.jonnie.elearning.utils.PaymentMethod;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final PaymentClient paymentClient;
    private final UserClient userClient;
    public String checkoutCart(String userId) {
        log.info("Starting checkout process for user: {}", userId);
        // Fetch the user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            log.warn("Cart is empty for user: {}", userId);
            throw new BusinessException("Cart is empty");
        }

        List<String> courseIds = cart.getCartItems().stream()
                .map(CartItem::getCourseId)
                .collect(Collectors.toList());

        List<String> instructorIds = cart.getCartItems().stream()
                .map(CartItem::getInstructorId)
                .distinct()
                .collect(Collectors.toList());

        UserResponse user = userClient.getUser()
                .orElseThrow(() -> new BusinessException("User not found"));

        PaymentRequest paymentRequest = new PaymentRequest(
                cart.getTotalAmount(),
                cart.getPaymentMethod(),
                user.getId(),
                cart.getCartId(),
                courseIds,
                instructorIds
        );

        log.info("Payment request prepared: {}", paymentRequest);

        try {
            ResponseEntity<Map<String, String>> response = paymentClient.requestToMakePayment(paymentRequest);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String approvalUrl = response.getBody().get("approvalUrl");
                log.info("Payment successful, redirecting to: {}", approvalUrl);

                cartRepository.delete(cart); // ✅ Delete the cart after successful payment initiation

                return approvalUrl; // ✅ Return the approval URL
            } else {
                log.error("Unexpected response from payment service: {}", response);
                throw new BusinessException("Payment processing failed");
            }
        } catch (FeignException e) {
            log.error("Error calling payment service: {}", e.getMessage(), e);
            throw new BusinessException("Payment request failed: " + e.getMessage());
        }
    }

    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Cart not found"));
        return new CartResponse(
                cart.getCartId(),
                cart.getTotalAmount()
        );
    }
}
