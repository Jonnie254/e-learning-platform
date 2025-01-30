package com.jonnie.elearning.repositories;

import com.jonnie.elearning.cart.Cart;
import com.jonnie.elearning.cartitem.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    @Query("SELECT c FROM CartItem c WHERE c.cart = ?1 AND c.courseId = ?2")
    Optional<CartItem> findByCartAndCourseId(Cart cart, String courseId);


    @Query("SELECT c FROM CartItem c WHERE c.userId = ?1")
    Page<CartItem> findAllByUserId(String userId, Pageable pageable);
}
