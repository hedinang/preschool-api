package org.example.preschool.repository;

import org.example.preschool.model.entity.SchoolMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchoolMessageRepository extends MongoRepository<SchoolMessage, String> {
}
