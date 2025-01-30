package com.jonnie.elearning.payment;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface PaymentRepository extends MongoRepository<PaymentRecord, String> {

}
