package org.example.preschool.model.dto;

import org.example.preschool.model.entity.Student;

public record StudentResponse(
        String id,
        String name,
        String gender,
        int age,
        String classroom,
        String parent,
        String phone,
        String parentEmail,
        String parentUserId,
        String avatar,
        String color,
        String status,
        String createdAt,
        String updatedAt,
        Long sequence,
        String parentName
) {

    public static StudentResponse from(Student student, String parentName) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getGender(),
                student.getAge(),
                student.getClassroom(),
                student.getParent(),
                student.getPhone(),
                student.getParentEmail(),
                student.getParentUserId(),
                student.getAvatar(),
                student.getColor(),
                student.getStatus(),
                student.getCreatedAt(),
                student.getUpdatedAt(),
                student.getSequence(),
                parentName
        );
    }
}
