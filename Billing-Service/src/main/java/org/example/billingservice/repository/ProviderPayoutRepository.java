package org.example.billingservice.repository;

import org.example.billingservice.model.entity.ProviderPayout;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProviderPayoutRepository extends MongoRepository<ProviderPayout,String> {
}
