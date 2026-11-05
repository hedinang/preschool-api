package org.example.preschool.repository;

import org.example.preschool.model.entity.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClassroomRepository extends MongoRepository<Classroom, String> {
}
