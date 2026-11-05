package org.example.preschool.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardSummary(
        long totalStudents,
        long presentStudents,
        long activeClassrooms,
        BigDecimal tuitionPaid,
        BigDecimal tuitionOutstanding,
        LocalDate date
) {
}
