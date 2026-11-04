package org.example.preschool.repository;

import org.example.preschool.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(String userId);
}
