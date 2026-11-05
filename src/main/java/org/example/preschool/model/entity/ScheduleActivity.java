package org.example.preschool.model.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "schedule_activity")
public class ScheduleActivity extends BaseEntity {

    @MongoId
    private String id;

    @NotBlank
    private String classroom;

    @NotBlank
    @Field(name = "day_of_week")
    private String dayOfWeek;

    @NotBlank
    @Field(name = "start_time")
    private String startTime;

    @NotBlank
    @Field(name = "end_time")
    private String endTime;

    @NotBlank
    private String title;

    @NotBlank
    private String kind;
}
