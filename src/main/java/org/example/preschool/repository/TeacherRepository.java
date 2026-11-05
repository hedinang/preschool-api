package org.example.preschool.repository;

import org.example.preschool.model.entity.Teacher;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeacherRepository extends MongoRepository<Teacher, String> {
}
