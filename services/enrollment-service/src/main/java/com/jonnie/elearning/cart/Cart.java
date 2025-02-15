package com.jonnie.elearning.cart;

import com.jonnie.elearning.cartitem.CartItem;
import com.jonnie.elearning.utils.CartStatus;
import com.jonnie.elearning.utils.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartId;
    private String userId;
    private String reference;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @CreatedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedAt;
}
