package org.example.preschool.controller.secure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.SchoolMessage;
import org.example.preschool.model.request.ListRequest;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.example.preschool.util.SequencePagination;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final PreschoolService preschoolService;

    @PostMapping("/list")
    public BaseResponse<List<SchoolMessage>> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) ListRequest request
    ) {
        ListRequest listRequest = request == null ? new ListRequest() : request;
        return Response.toData(SequencePagination.apply(
                preschoolService.getMessages(userDetails.getUser()),
                listRequest.getSequence(),
                listRequest.effectiveLimit(),
                listRequest.effectiveDirection()
        ));
    }

    @PostMapping
    public BaseResponse<SchoolMessage> createMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SchoolMessage message
    ) {
        return Response.toData(preschoolService.createMessage(userDetails.getUser(), message));
    }
}
