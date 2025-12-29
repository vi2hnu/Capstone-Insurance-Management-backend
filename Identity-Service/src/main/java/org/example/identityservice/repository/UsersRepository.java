package org.example.identityservice.repository;

import org.example.identityservice.model.entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends MongoRepository<Users, String> {
    Users findUsersByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
