package org.example.preschool.model.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

@MappedSuperclass
@SuperBuilder
@Getter
@Setter
public abstract class BaseEntity {
    private String status;
    @Field(name = "created_at")
    private String createdAt;
    @Field(name = "updated_at")
    private String updatedAt;
    @Field(name = "sequence")
    private Long sequence;

    public BaseEntity() {
    }
}
