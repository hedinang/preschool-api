package org.example.preschool.repository;

import org.example.preschool.model.entity.AttendanceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<AttendanceRecord, String> {

    List<AttendanceRecord> findByDate(LocalDate date);

    List<AttendanceRecord> findByDateAndStudentIdIn(LocalDate date, List<String> studentIds);

    Optional<AttendanceRecord> findByDateAndStudentId(LocalDate date, String studentId);
}
