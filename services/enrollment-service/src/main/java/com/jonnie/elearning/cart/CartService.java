package com.jonnie.elearning.cart;

import com.jonnie.elearning.cartitem.CartItem;
import com.jonnie.elearning.cartitem.CartItemResponse;
import com.jonnie.elearning.exceptions.BusinessException;
import com.jonnie.elearning.kafka.cart.CartConfirmation;
import com.jonnie.elearning.kafka.cart.CartConfirmationProducer;
import com.jonnie.elearning.kafka.cart.CartItemNotifyResponse;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final PaymentClient paymentClient;
    private final UserClient userClient;
    private final CartConfirmationProducer cartConfirmationProducer;

    // method to checkout the cart
    public String checkoutCart(String userId) {
        log.info("Starting checkout process for userId: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }
        List<String> courseIds = cart.getCartItems().stream()
                .map(CartItem::getCourseId)
                .collect(Collectors.toList());
        List<String> instructorIds = cart.getCartItems().stream()
                .map(CartItem::getInstructorId)
                .distinct()
                .collect(Collectors.toList());
        PaymentRequest paymentRequest = getPaymentRequest(
                cart, courseIds, instructorIds, userClient);
        try {
            ResponseEntity<Map<String, String>> response = paymentClient.requestToMakePayment(paymentRequest);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String approvalUrl = response.getBody().get("approvalUrl");
                log.info("Payment successful, redirecting to: {}", approvalUrl);

                cart.setStatus(CartStatus.PENDING);
                cartRepository.save(cart);
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
                                UserResponse.builder()
                                        .email(paymentRequest.customerEmail())
                                        .firstName(paymentRequest.customerFirstName())
                                        .lastName(paymentRequest.customerLastName())
                                        .build(),
                                cartItemNotifyResponses
                        )
                );
                return approvalUrl;
            } else {
                log.error("Unexpected response from payment service: {}", response);
                throw new BusinessException("Payment processing failed");
            }
        } catch (FeignException e) {
            log.error("Error calling payment service: {}", e.getMessage(), e);
            throw new BusinessException("Payment request failed: " + e.getMessage());
        }
    }

    //handle the payment success
    public void  handlePaymentSuccess(String cartReference){
        Cart cart = cartRepository.findByReference(cartReference)
                .orElseThrow(() -> new BusinessException("Cart not found"));
        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);
    }

    //handle the payment failure
    public void handlePaymentFailure(String cartReference){
        Cart cart = cartRepository.findByReference(cartReference)
                .orElseThrow(() -> new BusinessException("Cart not found"));
        cart.setStatus(CartStatus.ACTIVE);
        cartRepository.save(cart);
    }

    private static PaymentRequest getPaymentRequest(Cart cart,
                                                    List<String> courseIds,
                                                    List<String> instructorIds,
                                                    UserClient userClient) {
        UserResponse user = userClient.getUser()
                .orElseThrow(() -> new BusinessException("User not found"));
        return new PaymentRequest(
                cart.getTotalAmount(),
                cart.getPaymentMethod(),
                cart.getReference(),
                user.getId(),
                cart.getCartId(),
                user.firstName,
                user.lastName,
                user.email,
                courseIds,
                instructorIds
        );
    }

    public CartResponse getCart(String userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE);

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
