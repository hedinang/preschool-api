package org.example.preschool.model.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "tuition_invoice")
public class TuitionInvoice extends BaseEntity {

    @MongoId
    private String id;

    @NotBlank
    @Field(name = "student_id")
    private String studentId;

    @NotBlank
    private String name;

    @NotBlank
    private String room;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    private LocalDate due;

    @NotBlank
    @NotBlank
    private String month;

    @Field(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;

    private String avatar;

    private String color;
}
