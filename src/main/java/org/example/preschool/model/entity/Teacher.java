package org.example.preschool.model.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "teacher")
public class Teacher extends BaseEntity {

    @MongoId
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String title;

    @NotBlank
    private String classroom;

    private String phone;

    @Email
    private String email;

    private String experience;

    private String avatar;

    private String color;
}
