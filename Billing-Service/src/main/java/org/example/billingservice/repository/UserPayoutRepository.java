package org.example.billingservice.repository;

import org.example.billingservice.model.entity.UserPayout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPayoutRepository extends MongoRepository<UserPayout, String> {
}
