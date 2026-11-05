package org.example.preschool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.Teacher;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teachers")
public class TeacherController {

    private final PreschoolService preschoolService;

    @GetMapping
    public BaseResponse<List<Teacher>> getTeachers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Response.toData(preschoolService.getTeachers(userDetails.getUser()));
    }

    @PostMapping
    public BaseResponse<Teacher> createTeacher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody Teacher teacher
    ) {
        return Response.toData(preschoolService.createTeacher(userDetails.getUser(), teacher));
    }

    @PutMapping("/{id}")
    public BaseResponse<Teacher> updateTeacher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody Teacher teacher
    ) {
        return Response.toData(preschoolService.updateTeacher(userDetails.getUser(), id, teacher));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteTeacher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        preschoolService.deleteTeacher(userDetails.getUser(), id);
        return Response.toData(null);
    }
}
