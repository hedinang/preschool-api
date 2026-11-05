package org.example.preschool.util;

import org.example.preschool.model.entity.Student;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SequencePaginationTest {

    @Test
    void sortsDescendingAndLimitsFirstPage() {
        List<Student> result = SequencePagination.apply(
                List.of(student(2), student(1), student(3)),
                null,
                2,
                "desc"
        );

        assertEquals(List.of(3L, 2L), result.stream().map(Student::getSequence).toList());
    }

    @Test
    void appliesSequenceCursorInRequestedDirection() {
        List<Student> result = SequencePagination.apply(
                List.of(student(1), student(2), student(3)),
                2L,
                10,
                "asc"
        );

        assertEquals(List.of(3L), result.stream().map(Student::getSequence).toList());
    }

    @Test
    void rejectsUnsupportedDirection() {
        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> SequencePagination.apply(List.of(student(1)), null, 10, "sideways")
        );
    }

    private static Student student(long sequence) {
        Student student = new Student();
        student.setSequence(sequence);
        return student;
    }
}
