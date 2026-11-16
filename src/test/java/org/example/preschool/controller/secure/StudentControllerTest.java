package org.example.preschool.controller.secure;

import org.example.preschool.model.dto.CustomUserDetails;
import org.example.preschool.model.entity.Student;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.ListRequest;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.service.PreschoolService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentControllerTest {

    @Test
    void listAddsParentNamesFromUsersInOneBatch() {
        PreschoolService preschoolService = mock(PreschoolService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StudentController controller = new StudentController(preschoolService, userRepository);
        User actor = new User();
        Student student = new Student();
        student.setId("student-1");
        student.setParentUserId("parent-1");
        student.setParent("Old parent name");
        when(preschoolService.getStudents(actor, null)).thenReturn(List.of(student));

        User parent = new User();
        parent.setUserId("parent-1");
        parent.setDisplayName("Parent Name");
        when(userRepository.findByUserIdIn(List.of("parent-1"))).thenReturn(List.of(parent));

        var response = controller.getStudents(new CustomUserDetails(actor), new ListRequest());

        assertEquals("Parent Name", response.getData().get(0).parentName());
        assertEquals("Old parent name", response.getData().get(0).parent());
        verify(userRepository).findByUserIdIn(List.of("parent-1"));
    }

    @Test
    void listSupportsStudentsWithoutParentUserId() {
        PreschoolService preschoolService = mock(PreschoolService.class);
        UserRepository userRepository = mock(UserRepository.class);
        StudentController controller = new StudentController(preschoolService, userRepository);
        User actor = new User();
        Student student = new Student();
        student.setId("student-1");
        student.setParent("Parent Name");
        when(preschoolService.getStudents(actor, null)).thenReturn(List.of(student));

        var response = controller.getStudents(new CustomUserDetails(actor), new ListRequest());

        assertEquals("Parent Name", response.getData().get(0).parentName());
        verify(userRepository, never()).findByUserIdIn(anyCollection());
    }
}
