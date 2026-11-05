package org.example.preschool.controller.secure;

import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.dto.ParentSummary;
import org.example.preschool.model.request.ListRequest;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.SequencePagination;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parent")
public class ParentController {

    private final PreschoolService preschoolService;

    @PostMapping("/list")
    public BaseResponse<List<ParentSummary>> getParents(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @org.springframework.web.bind.annotation.RequestBody(required = false) ListRequest request
    ) {
        ListRequest listRequest = request == null ? new ListRequest() : request;
        List<ParentSummary> parents = preschoolService.getParents(userDetails.getUser());
        return Response.toData(SequencePagination.apply(
                parents,
                listRequest.getSequence(),
                listRequest.effectiveLimit(),
                listRequest.effectiveDirection(),
                ParentSummary::sequence
        ));
    }
}
