package org.example.preschool.controller.secure;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.dto.StudentResponse;
import org.example.preschool.model.entity.Student;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.ListRequest;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.response.BaseResponse;
import org.example.preschool.util.SequencePagination;
import org.example.preschool.util.response.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentController {

    private final PreschoolService preschoolService;
    private final UserRepository userRepository;

    @PostMapping("/list")
    public BaseResponse<List<StudentResponse>> getStudents(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) ListRequest request
    ) {
        ListRequest listRequest = request == null ? new ListRequest() : request;
        List<Student> students = SequencePagination.apply(
                preschoolService.getStudents(userDetails.getUser(), listRequest.getClassroom()),
                listRequest.getSequence(),
                listRequest.effectiveLimit(),
                listRequest.effectiveDirection()
        );
        List<String> parentUserIds = students.stream()
                .map(Student::getParentUserId)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
        Map<String, String> parentNames = parentUserIds.isEmpty()
                ? Map.of()
                : userRepository.findByUserIdIn(parentUserIds).stream()
                        .collect(Collectors.toMap(
                                User::getUserId,
                                user -> StringUtils.hasText(user.getDisplayName())
                                        ? user.getDisplayName()
                                        : Objects.requireNonNullElse(user.getUsername(), ""),
                                (existing, ignored) -> existing
                        ));

        return Response.toData(students.stream()
                .map(student -> {
                    String parentUserId = student.getParentUserId();
                    String parentName = StringUtils.hasText(parentUserId)
                            ? parentNames.getOrDefault(parentUserId, student.getParent())
                            : student.getParent();
                    return StudentResponse.from(student, parentName);
                })
                .toList());
    }

    @org.springframework.web.bind.annotation.GetMapping("/{id}")
    public BaseResponse<Student> getStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return Response.toData(preschoolService.getStudent(userDetails.getUser(), id));
    }

    @PostMapping
    public BaseResponse<Student> createStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody Student student
    ) {
        return Response.toData(preschoolService.createStudent(userDetails.getUser(), student));
    }

    @PutMapping("/{id}")
    public BaseResponse<Student> updateStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody Student student
    ) {
        return Response.toData(preschoolService.updateStudent(userDetails.getUser(), id, student));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteStudent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        preschoolService.deleteStudent(userDetails.getUser(), id);
        return Response.toData(null);
    }

}
