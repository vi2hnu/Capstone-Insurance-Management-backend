package org.example.billingservice.Repository;

import org.example.billingservice.model.entity.UserPayout;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPayoutRepository extends MongoRepository<UserPayout, String> {
}
