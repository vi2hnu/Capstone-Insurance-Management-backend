package org.example.billingservice.repository;

import org.example.billingservice.model.entity.ProviderPayout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderPayoutRepository extends MongoRepository<ProviderPayout,String> {
}
