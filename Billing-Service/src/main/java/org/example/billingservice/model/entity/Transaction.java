package org.example.billingservice.model.entity;

import lombok.Data;
import org.example.billingservice.model.enums.Purpose;
import org.example.billingservice.model.enums.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "Transactions")
public class Transaction {
    @Id
    String id;
    String orderId;
    String paymentId;
    Double amount;
    String userId;
    Purpose paymentPurpose;
    Status status;
    LocalDateTime createdAt;

    public Transaction(String orderId, String paymentId, Double amount, String userId, Purpose paymentPurpose, Status status) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.userId = userId;
        this.paymentPurpose = paymentPurpose;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}
