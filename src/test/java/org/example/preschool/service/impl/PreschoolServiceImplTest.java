package org.example.preschool.service.impl;

import org.example.preschool.model.entity.AttendanceRecord;
import org.example.preschool.model.entity.Student;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.AttendanceBatchRequest;
import org.example.preschool.repository.AttendanceRepository;
import org.example.preschool.repository.ClassroomRepository;
import org.example.preschool.repository.ScheduleActivityRepository;
import org.example.preschool.repository.SchoolMessageRepository;
import org.example.preschool.repository.StudentRepository;
import org.example.preschool.repository.TeacherRepository;
import org.example.preschool.repository.TuitionInvoiceRepository;
import org.example.preschool.util.constant.RoleType;
import org.example.preschool.util.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreschoolServiceImplTest {

    private StudentRepository studentRepository;
    private AttendanceRepository attendanceRepository;
    private PreschoolServiceImpl preschoolService;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        attendanceRepository = mock(AttendanceRepository.class);
        preschoolService = new PreschoolServiceImpl(
                studentRepository,
                mock(TeacherRepository.class),
                mock(ClassroomRepository.class),
                mock(ScheduleActivityRepository.class),
                attendanceRepository,
                mock(TuitionInvoiceRepository.class),
                mock(SchoolMessageRepository.class)
        );
    }

    @Test
    void parentCanOnlyListLinkedChildren() {
        User parent = new User();
        parent.setUserId("parent-1");
        parent.setUsername("parent@example.test");
        parent.setRole(RoleType.PARENT);

        Student linked = student("student-1", "parent-1", "Mầm 1");
        Student unrelated = student("student-2", "parent-2", "Mầm 2");
        when(studentRepository.findAll()).thenReturn(List.of(linked, unrelated));

        assertEquals(List.of(linked), preschoolService.getStudents(parent, null));
        assertThrows(ServiceException.class, () -> preschoolService.getStudent(parent, "student-2"));
    }

    @Test
    void attendanceUpdatesStudentStatusAndStoresDatedRecord() {
        User teacher = new User();
        teacher.setRole(RoleType.TEACHER);
        teacher.setClassroom("Mầm 1");

        Student student = student("student-1", null, "Mầm 1");
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(attendanceRepository.findByDateAndStudentId(any(), any())).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(AttendanceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDate date = LocalDate.of(2026, 11, 4);
        var saved = preschoolService.saveAttendance(
                teacher,
                new AttendanceBatchRequest(date, List.of(
                        new AttendanceBatchRequest.AttendanceItem("student-1", "Đã đến", "07:42")
                ))
        );

        assertEquals(1, saved.size());
        assertEquals(date, saved.get(0).getDate());
        assertEquals("Đã đến", saved.get(0).getStatus());
        assertEquals("Đã đến", student.getStatus());
        assertNotNull(saved.get(0).getId());
        verify(studentRepository).save(student);
    }

    @Test
    void teacherCannotMarkAttendanceForAnotherClassroom() {
        User teacher = new User();
        teacher.setRole(RoleType.TEACHER);
        teacher.setClassroom("Mầm 1");
        Student student = student("student-2", null, "Lá 1");
        when(studentRepository.findById("student-2")).thenReturn(Optional.of(student));

        assertThrows(ServiceException.class, () -> preschoolService.saveAttendance(
                teacher,
                new AttendanceBatchRequest(LocalDate.now(), List.of(
                        new AttendanceBatchRequest.AttendanceItem("student-2", "Đã đến", null)
                ))
        ));
    }

    private Student student(String id, String parentId, String classroom) {
        Student student = new Student();
        student.setId(id);
        student.setName("Nguyễn Minh Khôi");
        student.setClassroom(classroom);
        student.setParentUserId(parentId);
        return student;
    }
}
