package org.example.preschool.controller;

import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.dto.DashboardSummary;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PreschoolService preschoolService;

    @GetMapping("/summary")
    public BaseResponse<DashboardSummary> getSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String month
    ) {
        return Response.toData(preschoolService.getDashboardSummary(
                userDetails.getUser(),
                date == null ? LocalDate.now() : date,
                month == null ? YearMonth.now().toString() : month
        ));
    }
}
