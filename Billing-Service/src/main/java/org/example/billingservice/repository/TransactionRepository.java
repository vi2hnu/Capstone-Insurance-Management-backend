package org.example.billingservice.repository;

import org.example.billingservice.model.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction,String> {
    Transaction findByOrderId(String orderId);
}
