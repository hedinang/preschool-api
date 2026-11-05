package org.example.preschool.model.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "student")
public class Student extends BaseEntity {

    @MongoId
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @Min(1)
    @Max(10)
    private int age;

    @NotBlank
    @Indexed
    private String classroom;

    private String parent;

    private String phone;

    @Field(name = "parent_email")
    private String parentEmail;

    @Field(name = "parent_user_id")
    private String parentUserId;

    private String avatar;

    private String color;
}
