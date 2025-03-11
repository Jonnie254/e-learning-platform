package com.jonnie.elearning.cart;

import com.jonnie.elearning.cartitem.CartItemResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.kafka.cart.CartConfirmation;
import com.jonnie.elearning.kafka.cart.CartConfirmationProducer;
import com.jonnie.elearning.kafka.cart.CartItemNotifyResponse;
import com.jonnie.elearning.openfeign.payment.CoursePaymentDetails;
import com.jonnie.elearning.openfeign.payment.PaymentClient;
import com.jonnie.elearning.openfeign.payment.PaymentRequest;
import com.jonnie.elearning.openfeign.user.UserClient;
import com.jonnie.elearning.openfeign.user.UserResponse;
import com.jonnie.elearning.repositories.CartRepository;
import com.jonnie.elearning.utils.CartStatus;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final PaymentClient paymentClient;
    private final UserClient userClient;
    private final CartConfirmationProducer cartConfirmationProducer;

    // Checkout the cart
    public String checkoutCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Cart not found for userId: " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new BusinessException("Cart is empty for userId: " + userId);
        }

        List<CoursePaymentDetails> courseDetails = cart.getCartItems().stream()
                .map(cartItem -> new CoursePaymentDetails(
                        cartItem.getCourseId(),
                        cartItem.getInstructorId(),
                        cartItem.getPrice()
                ))
                .toList();

        PaymentRequest paymentRequest = getPaymentRequest(cart, courseDetails);

        try {
            ResponseEntity<Map<String, String>> response = paymentClient.requestToMakePayment(paymentRequest);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String approvalUrl = response.getBody().get("approvalUrl");
                cart.setStatus(CartStatus.ACTIVE);
                cartRepository.save(cart);

                // Notify via Kafka
                List<CartItemNotifyResponse> cartItemNotifyResponses = cart.getCartItems().stream()
                        .map(cartItem -> new CartItemNotifyResponse(
                                cartItem.getCourseName(),
                                cartItem.getPrice()
                        ))
                        .toList();

                cartConfirmationProducer.sendCartConfirmation(
                        new CartConfirmation(
                                cart.getReference(),
                                cart.getTotalAmount(),
                                new UserResponse(
                                        paymentRequest.userId(),
                                        paymentRequest.customerFirstName(),
                                        paymentRequest.customerLastName(),
                                        paymentRequest.customerEmail()
                                ),
                                cartItemNotifyResponses
                        )
                );
                return approvalUrl;
            } else {
                throw new BusinessException("Payment processing failed");
            }
        } catch (FeignException.BadRequest e) {
            throw new BusinessException("Invalid payment request: " + e.getMessage());
        } catch (FeignException.NotFound e) {
            throw new BusinessException("Payment service unavailable: " + e.getMessage());
        } catch (FeignException e) {
            throw new BusinessException("Payment request failed: " + e.getMessage());
        }
    }

    // Handle payment success
    public void handlePaymentSuccess(String cartReference) {
        Cart cart = cartRepository.findByReference(cartReference)
                .orElseThrow(() -> new BusinessException("Cart not found with reference: " + cartReference));
        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);
        log.info("Cart successfully checked out. CartReference: {}", cartReference);
    }

    // Handle payment failure
    public void handlePaymentFailure(String cartReference) {
        Cart cart = cartRepository.findByReference(cartReference)
                .orElseThrow(() -> new BusinessException("Cart not found with reference: " + cartReference));

        cart.setStatus(CartStatus.ACTIVE);
        cartRepository.save(cart);
        log.info("Cart payment failed. Cart set back to ACTIVE. CartReference: {}", cartReference);
    }

    // Generate payment request
    private PaymentRequest getPaymentRequest(Cart cart, List<CoursePaymentDetails> courseDetails) {
        UserResponse user = userClient.getUser()
                .orElseThrow(() -> new BusinessException("User not found for cartId: " + cart.getCartId()));

        log.info("User found for cartId {}: {}", cart.getCartId(), user);

        return new PaymentRequest(
                cart.getTotalAmount(),
                cart.getPaymentMethod(),
                cart.getReference(),
                user.getId(),
                cart.getReference(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                courseDetails
        );
    }

    // Retrieve active cart
    public CartResponse getCart(String userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserIdAndStatus(
                userId, CartStatus.ACTIVE );
        if (cartOptional.isEmpty()) {
            return new CartResponse(
                    "",
                    BigDecimal.ZERO,
                    "",
                    CartStatus.ACTIVE,
                    List.of()
            );
        }

        Cart cart = cartOptional.get();
        return new CartResponse(
                cart.getCartId(),
                cart.getTotalAmount(),
                cart.getReference(),
                cart.getStatus(),
                cart.getCartItems().stream()
                        .map(cartItem -> new CartItemResponse(
                                cartItem.getCartItemId(),
                                cartItem.getCourseId(),
                                cartItem.getCourseName(),
                                cartItem.getInstructorName(),
                                cartItem.getCourseImageUrl(),
                                cartItem.getPrice()))
                        .toList()
        );
    }
}