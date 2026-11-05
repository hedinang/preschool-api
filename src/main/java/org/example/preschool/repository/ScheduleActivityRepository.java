package org.example.preschool.repository;

import org.example.preschool.model.entity.ScheduleActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleActivityRepository extends MongoRepository<ScheduleActivity, String> {
}
