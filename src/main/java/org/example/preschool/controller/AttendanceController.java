package org.example.preschool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.AttendanceRecord;
import org.example.preschool.model.request.AttendanceBatchRequest;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final PreschoolService preschoolService;

    @GetMapping
    public BaseResponse<List<AttendanceRecord>> getAttendance(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String classroom
    ) {
        return Response.toData(preschoolService.getAttendance(
                userDetails.getUser(),
                date == null ? LocalDate.now() : date,
                classroom
        ));
    }

    @PutMapping
    public BaseResponse<List<AttendanceRecord>> saveAttendance(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AttendanceBatchRequest request
    ) {
        return Response.toData(preschoolService.saveAttendance(userDetails.getUser(), request));
    }
}
