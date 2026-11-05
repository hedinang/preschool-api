package org.example.preschool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.SchoolMessage;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final PreschoolService preschoolService;

    @GetMapping
    public BaseResponse<List<SchoolMessage>> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return Response.toData(preschoolService.getMessages(userDetails.getUser()));
    }

    @PostMapping
    public BaseResponse<SchoolMessage> createMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SchoolMessage message
    ) {
        return Response.toData(preschoolService.createMessage(userDetails.getUser(), message));
    }
}
