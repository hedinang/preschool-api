package org.example.preschool.model.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "school_message")
public class SchoolMessage extends BaseEntity {

    @MongoId
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private String date;

    @NotBlank
    private String audience;

    private String classroom;

    @NotBlank
    private String tag;

    private String initials;

    private String tone;

    private List<String> recipientIds = new ArrayList<>();
}
