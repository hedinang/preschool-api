package org.example.preschool.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.preschool.util.constant.RoleType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "user")
public class User extends BaseEntity {

    @MongoId
    private String id;

    @Indexed(unique = true)
    @Field(name = "user_id")
    private String userId;

    @Indexed(unique = true)
    private String username;

    private String password;

    private RoleType role;

    @Field(name = "display_name")
    private String displayName;

    private String title;

    private String classroom;

    private String phone;

    @Field(name = "phone2")
    private String phone2;

    private String address;

    private String gender;

    @Field(name = "student_id")
    private String studentId;

    private String avatar;

    private String color;

    @Field(name = "access_token")
    private String accessToken;
}
