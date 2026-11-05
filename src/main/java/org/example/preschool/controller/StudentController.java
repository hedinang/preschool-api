package org.example.preschool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.Student;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudentController {

    private final PreschoolService preschoolService;

    @GetMapping("/students")
    public BaseResponse<List<Student>> getStudents(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String classroom
    ) {
        return Response.toData(preschoolService.getStudents(userDetails.getUser(), classroom));
    }

    @GetMapping("/students/{id}")
    public BaseResponse<Student> getStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return Response.toData(preschoolService.getStudent(userDetails.getUser(), id));
    }

    @PostMapping("/students")
    public BaseResponse<Student> createStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody Student student
    ) {
        return Response.toData(preschoolService.createStudent(userDetails.getUser(), student));
    }

    @PutMapping("/students/{id}")
    public BaseResponse<Student> updateStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody Student student
    ) {
        return Response.toData(preschoolService.updateStudent(userDetails.getUser(), id, student));
    }

    @DeleteMapping("/students/{id}")
    public BaseResponse<Void> deleteStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        preschoolService.deleteStudent(userDetails.getUser(), id);
        return Response.toData(null);
    }

    @GetMapping("/parents")
    public BaseResponse<?> getParents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Response.toData(preschoolService.getParents(userDetails.getUser()));
    }
}
