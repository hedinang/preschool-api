package org.example.preschool.controller.secure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.dto.ManagedUserResponse;
import org.example.preschool.model.request.CreateUserRequest;
import org.example.preschool.model.request.ListRequest;
import org.example.preschool.model.request.UpdateUserRequest;
import org.example.preschool.service.UserManagementService;
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
@RequestMapping("/api/user")
public class UserController {
    private final UserManagementService userManagementService;

    @PostMapping("/list")
    public BaseResponse<List<ManagedUserResponse>> getUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) ListRequest request
    ) {
        ListRequest listRequest = request == null ? new ListRequest() : request;
        return Response.toData(SequencePagination.apply(
                userManagementService.getUsers(
                        userDetails.getUser(),
                        listRequest.getRole(),
                        listRequest.getSearch()
                ),
                listRequest.getSequence(),
                listRequest.effectiveLimit(),
                listRequest.effectiveDirection(),
                account -> account.sequence() == null ? Long.MIN_VALUE : account.sequence()
        ));
    }

    @PostMapping
    public BaseResponse<ManagedUserResponse> createUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateUserRequest request
    ) {
        return Response.toData(userManagementService.createUser(userDetails.getUser(), request));
    }

    @PutMapping("/{id}")
    public BaseResponse<ManagedUserResponse> updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return Response.toData(userManagementService.updateUser(userDetails.getUser(), id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        userManagementService.deleteUser(userDetails.getUser(), id);
        return Response.toData(null);
    }
}
