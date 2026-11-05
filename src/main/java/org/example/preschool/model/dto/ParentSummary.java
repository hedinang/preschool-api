package org.example.preschool.model.dto;

import org.example.preschool.model.entity.Student;

import java.util.List;

public record ParentSummary(String name, String phone, long sequence, List<Student> children) {
}
