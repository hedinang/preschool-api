package org.example.preschool.model.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "attendance")
@CompoundIndex(name = "attendance_date_student", def = "{'date': 1, 'student_id': 1}", unique = true)
public class AttendanceRecord extends BaseEntity {

    @MongoId
    private String id;

    @NotNull
    private LocalDate date;

    @NotBlank
    @Field(name = "student_id")
    private String studentId;

    private String studentName;

    private String classroom;

    @NotBlank
    private String checkInTime;
}
