package org.example.preschool.model.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "classroom")
public class Classroom extends BaseEntity {

    @MongoId
    private String id;

    @NotBlank
    private String name;

    private String age;

    @Min(1)
    private int capacity;

    private String teacher;

    private String time;

    private String icon;

    private String tone;
}
