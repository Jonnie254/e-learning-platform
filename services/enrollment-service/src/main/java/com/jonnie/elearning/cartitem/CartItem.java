package com.jonnie.elearning.cartitem;

import com.jonnie.elearning.cart.Cart;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String cartItemId;
    private String userId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    private String courseId;
    private String courseName;
    private BigDecimal price;
    private String courseImageUrl;
    private String instructorName;
    private String instructorId;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedAt;
}
