package org.example.preschool.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.ManagedUserResponse;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.CreateUserRequest;
import org.example.preschool.model.request.UpdateUserRequest;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.service.UserManagementService;
import org.example.preschool.util.StringUtil;
import org.example.preschool.util.constant.EntityStatus;
import org.example.preschool.util.constant.ErrorCode;
import org.example.preschool.util.constant.RoleType;
import org.example.preschool.util.exception.ServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {
    private static final Set<RoleType> MANAGED_ROLES = Set.of(
            RoleType.ADMIN,
            RoleType.TEACHER,
            RoleType.PARENT
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ManagedUserResponse> getUsers(User actor, RoleType role, String search) {
        requireAdmin(actor);
        String normalizedSearch = StringUtils.hasText(search) ? search.trim().toLowerCase() : null;
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == null
                        || !EntityStatus.DELETED.name().equalsIgnoreCase(user.getStatus()))
                .filter(user -> isManagedRole(user.getRole()))
                .filter(user -> role == null || role == normalizeRole(user.getRole()))
                .filter(user -> matchesSearch(user, normalizedSearch))
                .map(ManagedUserResponse::from)
                .toList();
    }

    @Override
    public ManagedUserResponse createUser(User actor, CreateUserRequest request) {
        requireAdmin(actor);
        validateRole(request.role());
        String username = request.username().trim().toLowerCase();
        if (userRepository.findAll().stream()
                .anyMatch(user -> username.equalsIgnoreCase(user.getUsername()))) {
            throw new ServiceException(ErrorCode.E409.code(), "An account with this email already exists");
        }
        validatePhoneNumbers(request.role(), request.phone(), request.phone2(), null);
        User user = new User();
        user.setId(StringUtil.generateId());
        user.setUserId(StringUtil.generateId());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setDisplayName(request.name().trim());
        user.setTitle(normalizeOptional(request.title()));
        user.setClassroom(request.role() == RoleType.TEACHER
                ? normalizeOptional(request.classroom())
                : null);
        applyContactDetails(user, request.phone(), request.phone2(), request.address(), request.gender());
        user.setStatus(EntityStatus.ACTIVE.name());
        user.setCreatedAt(LocalDateTime.now().toString());
        user.setUpdatedAt(user.getCreatedAt());
        return ManagedUserResponse.from(userRepository.save(user));
    }

    @Override
    public ManagedUserResponse updateUser(User actor, String id, UpdateUserRequest request) {
        requireAdmin(actor);
        User user = findManagedUser(id);
        preventSelfModification(actor, user);
        validateRole(request.role());
        validatePhoneNumbers(request.role(), request.phone(), request.phone2(), user.getId());
        user.setRole(request.role());
        user.setDisplayName(request.name().trim());
        user.setTitle(normalizeOptional(request.title()));
        user.setClassroom(request.role() == RoleType.TEACHER
                ? normalizeOptional(request.classroom())
                : null);
        applyContactDetails(user, request.phone(), request.phone2(), request.address(), request.gender());
        if (StringUtils.hasText(request.password())) {
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setAccessToken(null);
        }
        user.setUpdatedAt(LocalDateTime.now().toString());
        return ManagedUserResponse.from(userRepository.save(user));
    }

    @Override
    public void deleteUser(User actor, String id) {
        requireAdmin(actor);
        User user = findManagedUser(id);
        preventSelfModification(actor, user);
        user.setStatus(EntityStatus.DELETED.name());
        user.setAccessToken(null);
        user.setUpdatedAt(LocalDateTime.now().toString());
        userRepository.save(user);
    }

    private User findManagedUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.E404.code(), "User not found"));
        if (!isManagedRole(user.getRole())
                || EntityStatus.DELETED.name().equalsIgnoreCase(user.getStatus())) {
            throw new ServiceException(ErrorCode.E404.code(), "User not found");
        }
        return user;
    }

    private void preventSelfModification(User actor, User target) {
        if ((actor.getId() != null && Objects.equals(actor.getId(), target.getId()))
                || (actor.getUserId() != null && Objects.equals(actor.getUserId(), target.getUserId()))) {
            throw new ServiceException(ErrorCode.E400.code(), "You cannot modify your own account here");
        }
    }

    private void requireAdmin(User actor) {
        if (actor == null || (actor.getRole() != RoleType.ADMIN && actor.getRole() != RoleType.SUPER_ADMIN)) {
            throw new ServiceException(ErrorCode.E403.code(), "Administrator access is required");
        }
    }

    private void validateRole(RoleType role) {
        if (!MANAGED_ROLES.contains(role)) {
            throw new ServiceException(ErrorCode.E400.code(), "Only ADMIN, TEACHER and PARENT roles can be managed");
        }
    }

    private boolean isManagedRole(RoleType role) {
        return role != null && (MANAGED_ROLES.contains(role) || role == RoleType.CUSTOMER);
    }

    private RoleType normalizeRole(RoleType role) {
        return role == RoleType.CUSTOMER ? RoleType.PARENT : role;
    }

    private boolean matchesSearch(User user, String search) {
        if (search == null) {
            return true;
        }
        return containsIgnoreCase(user.getDisplayName(), search)
                || containsIgnoreCase(user.getUsername(), search)
                || containsIgnoreCase(user.getPhone(), search)
                || containsIgnoreCase(user.getPhone2(), search);
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }

    private String normalizeOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void applyContactDetails(User user, String phone, String phone2, String address, String gender) {
        user.setPhone(normalizeOptional(phone));
        user.setPhone2(normalizeOptional(phone2));
        user.setAddress(normalizeOptional(address));
        user.setGender(normalizeOptional(gender));
    }

    private void validatePhoneNumbers(RoleType role, String phone, String phone2, String userId) {
        String normalizedPhone = normalizePhone(phone);
        String normalizedPhone2 = normalizePhone(phone2);
        if (normalizedPhone == null) {
            return;
        }
        if (normalizedPhone.equals(normalizedPhone2)) {
            throw new ServiceException(ErrorCode.E409.code(), "Phone number 1 and phone number 2 must be different");
        }
        boolean alreadyUsedBySameRole = userRepository.findAll().stream()
                .filter(user -> !Objects.equals(userId, user.getId()))
                .filter(user -> !EntityStatus.DELETED.name().equalsIgnoreCase(user.getStatus()))
                .filter(user -> normalizeRole(user.getRole()) == normalizeRole(role))
                .anyMatch(user -> normalizedPhone.equals(normalizePhone(user.getPhone())));
        if (alreadyUsedBySameRole) {
            throw new ServiceException(
                    ErrorCode.E409.code(),
                    "Phone number 1 is already used by another account with the same role"
            );
        }
    }

    private String normalizePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        String digits = phone.replaceAll("\\D", "");
        return digits.isEmpty() ? null : digits;
    }
}
