package org.example.billingservice.model.entity;

import lombok.Data;
import org.example.billingservice.model.enums.Purpose;
import org.example.billingservice.model.enums.Status;
import org.example.billingservice.model.enums.UserType;
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
    UserType userType;
    String userId;
    Long providerId;
    Purpose paymentPurpose;
    Status status;
    LocalDateTime createdAt;
}
