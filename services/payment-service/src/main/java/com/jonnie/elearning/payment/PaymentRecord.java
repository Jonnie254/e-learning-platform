package com.jonnie.elearning.payment;

import com.jonnie.elearning.utils.PaymentMethod;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class PaymentRecord {
    @Id
    private String paymentId;
    private String userId;
    private String cartId;
    private String cartReference;
    private PaymentMethod paymentMethod;
    private boolean isPaid;
    private BigDecimal amount;
    private List<CoursePaymentDetails> coursePaymentDetails;

    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;

   @CreatedDate
   private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
