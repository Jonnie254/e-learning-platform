package com.jonnie.elearning.payment;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PaymentRepository extends MongoRepository<PaymentRecord, String> {

    @Query(value = "{ 'isPaid' : ?0 }", fields = "{ 'amount' : 1 }")
    List<PaymentRecord> findAllByIsPaid(boolean b);
}
