package org.example.billingservice.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class UserPayout {
    @Id
    String id;
    String userId;
    Long claimId;
    Double amount;

    public UserPayout(String userId, Long claimId, Double amount) {
        this.userId = userId;
        this.claimId = claimId;
        this.amount = amount;
    }
}
