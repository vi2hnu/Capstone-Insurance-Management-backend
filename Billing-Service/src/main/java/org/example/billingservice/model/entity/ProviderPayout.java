package org.example.billingservice.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@EnableMongoAuditing
public class ProviderPayout {
    @Id
    String id;
    Long providerId;
    Long claimId;
    Double amount;

    public ProviderPayout(Long providerId, Long claimId, Double amount) {
        this.providerId = providerId;
        this.claimId = claimId;
        this.amount = amount;
    }
}
