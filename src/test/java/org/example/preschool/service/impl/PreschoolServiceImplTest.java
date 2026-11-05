package org.example.preschool.service.impl;

import org.example.preschool.model.entity.AttendanceRecord;
import org.example.preschool.model.entity.Student;
import org.example.preschool.model.entity.Teacher;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.AttendanceBatchRequest;
import org.example.preschool.repository.AttendanceRepository;
import org.example.preschool.repository.ClassroomRepository;
import org.example.preschool.repository.ScheduleActivityRepository;
import org.example.preschool.repository.SchoolMessageRepository;
import org.example.preschool.repository.StudentRepository;
import org.example.preschool.repository.TeacherRepository;
import org.example.preschool.repository.TuitionInvoiceRepository;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.util.constant.RoleType;
import org.example.preschool.util.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreschoolServiceImplTest {

    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private AttendanceRepository attendanceRepository;
    private UserRepository userRepository;
    private PreschoolServiceImpl preschoolService;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        teacherRepository = mock(TeacherRepository.class);
        attendanceRepository = mock(AttendanceRepository.class);
        userRepository = mock(UserRepository.class);
        preschoolService = new PreschoolServiceImpl(
                studentRepository,
                teacherRepository,
                mock(ClassroomRepository.class),
                mock(ScheduleActivityRepository.class),
                attendanceRepository,
                mock(TuitionInvoiceRepository.class),
                mock(SchoolMessageRepository.class),
                userRepository
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
    void attendanceStoresDatedRecordWithoutOverwritingLifecycleStatus() {
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
        assertNull(student.getStatus());
        assertNotNull(saved.get(0).getId());
        verify(studentRepository, never()).save(student);
    }

    @Test
    void createsStudentWithActiveLifecycleStatus() {
        User admin = new User();
        admin.setRole(RoleType.ADMIN);
        Student student = student(null, null, "Mầm 1");
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student created = preschoolService.createStudent(admin, student);

        assertEquals("ACTIVE", created.getStatus());
        assertNotNull(created.getId());
    }

    @Test
    void adminCanCreateStudentLinkedToExistingParentAccount() {
        User admin = new User();
        admin.setRole(RoleType.ADMIN);
        User parent = new User();
        parent.setUserId("parent-user-1");
        parent.setUsername("parent@example.test");
        parent.setDisplayName("Parent Name");
        parent.setRole(RoleType.PARENT);
        parent.setStatus("ACTIVE");
        when(userRepository.findByUserId("parent-user-1")).thenReturn(Optional.of(parent));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Student student = student(null, null, "Mầm 1");
        student.setParentUserId("parent-user-1");
        student.setParentEmail("untrusted@example.test");
        student.setParent("Untrusted Name");

        Student created = preschoolService.createStudent(admin, student);

        assertEquals("parent-user-1", created.getParentUserId());
        assertEquals("parent@example.test", created.getParentEmail());
        assertEquals("Parent Name", created.getParent());
    }

    @Test
    void adminCannotLinkStudentToNonParentAccount() {
        User admin = new User();
        admin.setRole(RoleType.ADMIN);
        User teacher = new User();
        teacher.setUserId("teacher-user-1");
        teacher.setRole(RoleType.TEACHER);
        teacher.setStatus("ACTIVE");
        when(userRepository.findByUserId("teacher-user-1")).thenReturn(Optional.of(teacher));
        Student student = student(null, null, "Mầm 1");
        student.setParentUserId("teacher-user-1");

        assertThrows(ServiceException.class, () -> preschoolService.createStudent(admin, student));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void parentCanDeclareChildLinkedToTheirAccountWithoutClassAssignment() {
        User parent = new User();
        parent.setRole(RoleType.PARENT);
        parent.setUserId("parent-1");
        parent.setUsername("parent@example.test");
        parent.setDisplayName("Phụ huynh");
        Student student = student(null, null, null);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student created = preschoolService.createStudent(parent, student);

        assertEquals("ACTIVE", created.getStatus());
        assertEquals("parent-1", created.getParentUserId());
        assertEquals("parent@example.test", created.getParentEmail());
        assertEquals("Phụ huynh", created.getParent());
        assertNull(created.getClassroom());
    }

    @Test
    void teacherCanEditStudentInTheirOwnClass() {
        User teacher = new User();
        teacher.setRole(RoleType.TEACHER);
        teacher.setClassroom("Mầm 1");
        Student existing = student("student-1", null, "Mầm 1");
        existing.setStatus("ACTIVE");
        Student update = student(null, null, "Mầm 1");
        update.setName("Updated name");
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student saved = preschoolService.updateStudent(teacher, "student-1", update);

        assertEquals("Updated name", saved.getName());
        assertEquals("ACTIVE", saved.getStatus());
    }

    @Test
    void deletingStudentMarksItDeletedWithoutRemovingDocument() {
        User admin = new User();
        admin.setRole(RoleType.ADMIN);
        Student student = student("student-1", null, "Mầm 1");
        student.setStatus("ACTIVE");
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        preschoolService.deleteStudent(admin, "student-1");

        assertEquals("DELETED", student.getStatus());
        verify(studentRepository).save(student);
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void updatingStudentPreservesLifecycleAndParentAccountLinks() {
        User admin = new User();
        admin.setRole(RoleType.ADMIN);
        Student existing = student("student-1", null, "Mầm 1");
        existing.setStatus("ACTIVE");
        existing.setParentEmail("parent@example.test");
        existing.setParentUserId("parent-1");
        Student update = student(null, null, "Mầm 2");
        update.setName("Updated name");
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student saved = preschoolService.updateStudent(admin, "student-1", update);

        assertEquals("ACTIVE", saved.getStatus());
        assertEquals("parent@example.test", saved.getParentEmail());
        assertEquals("parent-1", saved.getParentUserId());
        assertEquals("Mầm 2", saved.getClassroom());
    }

    @Test
    void doesNotReturnSoftDeletedStudents() {
        User admin = new User();
        admin.setRole(RoleType.ADMIN);
        Student active = student("student-1", null, "Mầm 1");
        Student deleted = student("student-2", null, "Mầm 1");
        deleted.setStatus("DELETED");
        when(studentRepository.findAll()).thenReturn(List.of(active, deleted));

        assertEquals(List.of(active), preschoolService.getStudents(admin, null));
    }

    @Test
    void deletingTeacherMarksItDeletedWithoutRemovingDocument() {
        User admin = new User();
        admin.setRole(RoleType.ADMIN);
        Teacher teacher = new Teacher();
        teacher.setId("teacher-1");
        teacher.setStatus("ACTIVE");
        when(teacherRepository.findById("teacher-1")).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        preschoolService.deleteTeacher(admin, "teacher-1");

        assertEquals("DELETED", teacher.getStatus());
        verify(teacherRepository).save(teacher);
        verify(teacherRepository, never()).delete(any(Teacher.class));
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
