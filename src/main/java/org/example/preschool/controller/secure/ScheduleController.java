package org.example.preschool.controller.secure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.ScheduleActivity;
import org.example.preschool.model.request.ListRequest;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.example.preschool.util.SequencePagination;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final PreschoolService preschoolService;

    @PostMapping("/list")
    public BaseResponse<List<ScheduleActivity>> getSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) ListRequest request
    ) {
        ListRequest listRequest = request == null ? new ListRequest() : request;
        return Response.toData(SequencePagination.apply(
                preschoolService.getSchedule(userDetails.getUser(), listRequest.getClassroom()),
                listRequest.getSequence(),
                listRequest.effectiveLimit(),
                listRequest.effectiveDirection()
        ));
    }

    @PostMapping
    public BaseResponse<ScheduleActivity> createActivity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ScheduleActivity activity
    ) {
        return Response.toData(preschoolService.createScheduleActivity(userDetails.getUser(), activity));
    }

    @PutMapping("/{id}")
    public BaseResponse<ScheduleActivity> updateActivity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody ScheduleActivity activity
    ) {
        return Response.toData(preschoolService.updateScheduleActivity(userDetails.getUser(), id, activity));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteActivity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        preschoolService.deleteScheduleActivity(userDetails.getUser(), id);
        return Response.toData(null);
    }
}
