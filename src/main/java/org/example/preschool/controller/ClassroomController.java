package org.example.preschool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.Classroom;
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
@RequestMapping("/api/classes")
public class ClassroomController {

    private final PreschoolService preschoolService;

    @GetMapping
    public BaseResponse<List<Classroom>> getClassrooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Response.toData(preschoolService.getClassrooms(userDetails.getUser()));
    }

    @PostMapping
    public BaseResponse<Classroom> createClassroom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody Classroom classroom
    ) {
        return Response.toData(preschoolService.createClassroom(userDetails.getUser(), classroom));
    }

    @PutMapping("/{id}")
    public BaseResponse<Classroom> updateClassroom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody Classroom classroom
    ) {
        return Response.toData(preschoolService.updateClassroom(userDetails.getUser(), id, classroom));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteClassroom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        preschoolService.deleteClassroom(userDetails.getUser(), id);
        return Response.toData(null);
    }
}
