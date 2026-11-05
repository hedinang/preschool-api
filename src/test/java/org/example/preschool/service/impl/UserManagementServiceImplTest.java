package org.example.preschool.service.impl;

import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.CreateUserRequest;
import org.example.preschool.model.request.UpdateUserRequest;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.util.constant.RoleType;
import org.example.preschool.util.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserManagementServiceImplTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserManagementServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void createsOnlySupportedAccountTypesAndHashesPassword() {
        User actor = admin();
        when(userRepository.findAll()).thenReturn(List.of());
        when(passwordEncoder.encode("secret1")).thenReturn("hashed-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var created = service.createUser(
                actor,
                new CreateUserRequest(
                        " TEACHER@example.test ", "secret1", RoleType.TEACHER, "Teacher",
                        "Lead teacher", "Mầm 1", "0900000001", "0900000002", "Hà Nội", "Nữ"
                )
        );

        assertEquals("teacher@example.test", created.username());
        assertEquals(RoleType.TEACHER, created.role());
        assertEquals("ACTIVE", created.status());
        assertEquals("0900000001", created.phone());
        assertEquals("0900000002", created.phone2());
        assertEquals("Hà Nội", created.address());
        assertEquals("Nữ", created.gender());
        verify(passwordEncoder).encode("secret1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void rejectsStudentRoleForManagedAccounts() {
        assertThrows(ServiceException.class, () -> service.createUser(
                admin(),
                new CreateUserRequest(
                        "student@example.test", "secret1", RoleType.STUDENT, "Student",
                        null, null, null, null, null, null
                )
        ));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void rejectsMatchingPhoneNumbersOnSameAccount() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThrows(ServiceException.class, () -> service.createUser(
                admin(),
                createRequest(RoleType.PARENT, "090-123 4567", "0901234567")
        ));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void rejectsPhoneOneAlreadyUsedByAnotherAccountWithSameRole() {
        User existingParent = new User();
        existingParent.setId("existing-parent");
        existingParent.setRole(RoleType.PARENT);
        existingParent.setPhone("090 123 4567");
        existingParent.setStatus("ACTIVE");
        when(userRepository.findAll()).thenReturn(List.of(existingParent));

        assertThrows(ServiceException.class, () -> service.createUser(
                admin(),
                createRequest(RoleType.PARENT, "090-123-4567", "0907654321")
        ));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void allowsPhoneOneToBeSharedAcrossDifferentRoles() {
        User existingTeacher = new User();
        existingTeacher.setId("existing-teacher");
        existingTeacher.setRole(RoleType.TEACHER);
        existingTeacher.setPhone("0901234567");
        existingTeacher.setStatus("ACTIVE");
        when(userRepository.findAll()).thenReturn(List.of(existingTeacher));
        when(passwordEncoder.encode("secret1")).thenReturn("hashed-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var created = service.createUser(
                admin(),
                createRequest(RoleType.ADMIN, "090-123-4567", "0907654321")
        );

        assertEquals(RoleType.ADMIN, created.role());
        assertEquals("090-123-4567", created.phone());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void rejectsPhoneOneAlreadyUsedByAnotherAccountWhenUpdating() {
        User existingParent = new User();
        existingParent.setId("target-id");
        existingParent.setUserId("target-user-id");
        existingParent.setRole(RoleType.PARENT);
        existingParent.setStatus("ACTIVE");
        User otherParent = new User();
        otherParent.setId("other-parent");
        otherParent.setRole(RoleType.CUSTOMER);
        otherParent.setPhone("0901234567");
        otherParent.setStatus("ACTIVE");
        when(userRepository.findById("target-id")).thenReturn(Optional.of(existingParent));
        when(userRepository.findAll()).thenReturn(List.of(otherParent));

        assertThrows(ServiceException.class, () -> service.updateUser(
                admin(),
                "target-id",
                new UpdateUserRequest(
                        RoleType.PARENT, "Parent Name", null, null, null,
                        "090-123-4567", "0907654321", null, null
                )
        ));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void exposesLegacyCustomerAccountsAsParents() {
        User legacyParent = new User();
        legacyParent.setUsername("legacy@example.test");
        legacyParent.setRole(RoleType.CUSTOMER);
        legacyParent.setStatus("ACTIVE");
        when(userRepository.findAll()).thenReturn(List.of(legacyParent));

        var users = service.getUsers(admin(), null, null);

        assertEquals(1, users.size());
        assertEquals(RoleType.PARENT, users.get(0).role());
    }

    @Test
    void filtersUsersByRequestedRoleAndIncludesLegacyParents() {
        User teacher = new User();
        teacher.setRole(RoleType.TEACHER);
        teacher.setStatus("ACTIVE");
        User parent = new User();
        parent.setRole(RoleType.PARENT);
        parent.setStatus("ACTIVE");
        User legacyParent = new User();
        legacyParent.setRole(RoleType.CUSTOMER);
        legacyParent.setStatus("ACTIVE");
        when(userRepository.findAll()).thenReturn(List.of(teacher, parent, legacyParent));

        var users = service.getUsers(admin(), RoleType.PARENT, null);

        assertEquals(2, users.size());
        assertEquals(List.of(RoleType.PARENT, RoleType.PARENT),
                users.stream().map(user -> user.role()).toList());
    }

    @Test
    void searchesParentAccountsByNameEmailAndPhone() {
        User parent = new User();
        parent.setDisplayName("Nguyễn An");
        parent.setUsername("an@example.test");
        parent.setPhone("0901234567");
        parent.setRole(RoleType.PARENT);
        parent.setStatus("ACTIVE");
        User other = new User();
        other.setDisplayName("Trần Bình");
        other.setUsername("binh@example.test");
        other.setRole(RoleType.PARENT);
        other.setStatus("ACTIVE");
        when(userRepository.findAll()).thenReturn(List.of(parent, other));

        assertEquals(1, service.getUsers(admin(), RoleType.PARENT, "nguyễn").size());
        assertEquals(1, service.getUsers(admin(), RoleType.PARENT, "an@example").size());
        assertEquals(1, service.getUsers(admin(), RoleType.PARENT, "090123").size());
    }

    @Test
    void updatesContactAndPersonalDetails() {
        User actor = admin();
        User target = new User();
        target.setId("target-id");
        target.setUserId("target-user-id");
        target.setRole(RoleType.PARENT);
        target.setStatus("ACTIVE");
        when(userRepository.findById("target-id")).thenReturn(Optional.of(target));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var updated = service.updateUser(
                actor,
                "target-id",
                new UpdateUserRequest(
                        RoleType.PARENT, "Parent Name", null, null, null,
                        "0900000001", "0900000002", "Hà Nội", "Nữ"
                )
        );

        assertEquals("0900000001", updated.phone());
        assertEquals("0900000002", updated.phone2());
        assertEquals("Hà Nội", updated.address());
        assertEquals("Nữ", updated.gender());
    }

    @Test
    void softDeletesManagedUsers() {
        User actor = admin();
        User target = new User();
        target.setId("target-id");
        target.setUserId("target-user-id");
        target.setRole(RoleType.PARENT);
        target.setStatus("ACTIVE");
        when(userRepository.findById("target-id")).thenReturn(Optional.of(target));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.deleteUser(actor, "target-id");

        assertEquals("DELETED", target.getStatus());
        verify(userRepository).save(target);
    }

    @Test
    void doesNotAllowParentToManageUsers() {
        User parent = new User();
        parent.setRole(RoleType.PARENT);

        assertThrows(ServiceException.class, () -> service.getUsers(parent, null, null));
    }

    private User admin() {
        User admin = new User();
        admin.setId("admin-id");
        admin.setUserId("admin-user-id");
        admin.setRole(RoleType.ADMIN);
        return admin;
    }

    private CreateUserRequest createRequest(RoleType role, String phone, String phone2) {
        return new CreateUserRequest(
                "account@example.test", "secret1", role, "Account", null, null,
                phone, phone2, null, null
        );
    }
}
