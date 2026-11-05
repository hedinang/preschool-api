package org.example.preschool.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.preschool.util.constant.RoleType;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ListRequest {

    private Long sequence;

    @Min(1)
    @Max(500)
    private Integer limit = 10;

    private String direction = "desc";
    private String classroom;
    private RoleType role;
    private String search;
    private String month;
    private LocalDate date;

    public int effectiveLimit() {
        return limit == null ? 10 : limit;
    }

    public String effectiveDirection() {
        return direction == null || direction.isBlank() ? "desc" : direction;
    }
}
