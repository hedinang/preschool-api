package org.example.preschool.service;

import org.example.preschool.model.dto.ManagedUserResponse;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.CreateUserRequest;
import org.example.preschool.model.request.UpdateUserRequest;
import org.example.preschool.util.constant.RoleType;

import java.util.List;

public interface UserManagementService {
    List<ManagedUserResponse> getUsers(User actor, RoleType role, String search);

    ManagedUserResponse createUser(User actor, CreateUserRequest request);

    ManagedUserResponse updateUser(User actor, String id, UpdateUserRequest request);

    void deleteUser(User actor, String id);
}
