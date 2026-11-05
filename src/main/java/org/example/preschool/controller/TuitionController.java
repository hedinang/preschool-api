package org.example.preschool.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.TuitionInvoice;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/tuition")
public class TuitionController {

    private final PreschoolService preschoolService;

    @GetMapping
    public BaseResponse<List<TuitionInvoice>> getInvoices(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String classroom
    ) {
        return Response.toData(preschoolService.getTuition(userDetails.getUser(), month, classroom));
    }

    @PostMapping
    public BaseResponse<TuitionInvoice> createInvoice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TuitionInvoice invoice
    ) {
        return Response.toData(preschoolService.createInvoice(userDetails.getUser(), invoice));
    }

    @PutMapping("/{id}")
    public BaseResponse<TuitionInvoice> updateInvoice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody TuitionInvoice invoice
    ) {
        return Response.toData(preschoolService.updateInvoice(userDetails.getUser(), id, invoice));
    }

    @PostMapping("/remind")
    public BaseResponse<List<TuitionInvoice>> sendOutstandingReminders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String month
    ) {
        return Response.toData(preschoolService.sendInvoiceReminders(userDetails.getUser(), month));
    }

    @PostMapping("/{id}/remind")
    public BaseResponse<TuitionInvoice> sendReminder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return Response.toData(preschoolService.sendInvoiceReminder(userDetails.getUser(), id));
    }
}
