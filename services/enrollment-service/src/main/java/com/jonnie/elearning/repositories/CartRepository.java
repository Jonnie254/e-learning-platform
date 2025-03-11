package com.jonnie.elearning.repositories;

import com.jonnie.elearning.cart.Cart;
import com.jonnie.elearning.utils.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    @Query("SELECT c FROM Cart c WHERE c.status = 'PENDING' OR" +
            "  c.status = 'ACTIVE' and c.userId = :userId")
    Optional<Cart> findByUserId(String userId);

     Optional<Cart> findByReference(String cartReference);

    Optional<Cart> findByUserIdAndStatusIn(String userId, List<CartStatus> statuses);

    Optional <Cart> findByUserIdAndStatus(String userId, CartStatus status);

}
