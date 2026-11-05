package org.example.preschool.controller.secure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.TuitionInvoice;
import org.example.preschool.model.request.ListRequest;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.response.Response;
import org.example.preschool.util.SequencePagination;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/list")
    public BaseResponse<List<TuitionInvoice>> getInvoices(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) ListRequest request
    ) {
        ListRequest listRequest = request == null ? new ListRequest() : request;
        return Response.toData(SequencePagination.apply(
                preschoolService.getTuition(
                        userDetails.getUser(), listRequest.getMonth(), listRequest.getClassroom()
                ),
                listRequest.getSequence(),
                listRequest.effectiveLimit(),
                listRequest.effectiveDirection()
        ));
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
