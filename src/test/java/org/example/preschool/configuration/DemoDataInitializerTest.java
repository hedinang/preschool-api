package org.example.preschool.configuration;

import org.example.preschool.model.entity.AttendanceRecord;
import org.example.preschool.model.entity.Classroom;
import org.example.preschool.model.entity.ScheduleActivity;
import org.example.preschool.model.entity.SchoolMessage;
import org.example.preschool.model.entity.Student;
import org.example.preschool.model.entity.Teacher;
import org.example.preschool.model.entity.TuitionInvoice;
import org.example.preschool.model.entity.User;
import org.example.preschool.repository.AttendanceRepository;
import org.example.preschool.repository.ClassroomRepository;
import org.example.preschool.repository.ScheduleActivityRepository;
import org.example.preschool.repository.SchoolMessageRepository;
import org.example.preschool.repository.StudentRepository;
import org.example.preschool.repository.TeacherRepository;
import org.example.preschool.repository.TuitionInvoiceRepository;
import org.example.preschool.repository.UserRepository;
import org.example.preschool.util.constant.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoDataInitializerTest {

    @Test
    void seedsFakeDataOnceAndKeepsExistingRecordsOnSubsequentRuns() throws Exception {
        StudentRepository studentRepository = mock(StudentRepository.class);
        TeacherRepository teacherRepository = mock(TeacherRepository.class);
        ClassroomRepository classroomRepository = mock(ClassroomRepository.class);
        ScheduleActivityRepository scheduleRepository = mock(ScheduleActivityRepository.class);
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        TuitionInvoiceRepository tuitionRepository = mock(TuitionInvoiceRepository.class);
        SchoolMessageRepository messageRepository = mock(SchoolMessageRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        List<Student> students = new ArrayList<>();
        List<Teacher> teachers = new ArrayList<>();
        List<Classroom> classrooms = new ArrayList<>();
        List<ScheduleActivity> schedules = new ArrayList<>();
        List<AttendanceRecord> attendance = new ArrayList<>();
        List<TuitionInvoice> invoices = new ArrayList<>();
        List<SchoolMessage> messages = new ArrayList<>();
        List<User> users = new ArrayList<>();

        when(studentRepository.findAll()).thenReturn(students);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> add(students, invocation.getArgument(0)));
        when(teacherRepository.findAll()).thenReturn(teachers);
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> add(teachers, invocation.getArgument(0)));
        when(classroomRepository.findAll()).thenReturn(classrooms);
        when(classroomRepository.save(any(Classroom.class))).thenAnswer(invocation -> add(classrooms, invocation.getArgument(0)));
        when(scheduleRepository.findAll()).thenReturn(schedules);
        when(scheduleRepository.save(any(ScheduleActivity.class))).thenAnswer(invocation -> add(schedules, invocation.getArgument(0)));
        when(attendanceRepository.findByDate(any(LocalDate.class))).thenAnswer(invocation -> attendance.stream()
                .filter(record -> invocation.getArgument(0).equals(record.getDate()))
                .toList());
        when(attendanceRepository.save(any(AttendanceRecord.class))).thenAnswer(invocation -> add(attendance, invocation.getArgument(0)));
        when(tuitionRepository.findAll()).thenReturn(invoices);
        when(tuitionRepository.save(any(TuitionInvoice.class))).thenAnswer(invocation -> add(invoices, invocation.getArgument(0)));
        when(messageRepository.findAll()).thenReturn(messages);
        when(messageRepository.save(any(SchoolMessage.class))).thenAnswer(invocation -> add(messages, invocation.getArgument(0)));
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> add(users, invocation.getArgument(0)));
        when(passwordEncoder.encode("123456")).thenReturn("encoded-demo-password");

        DemoDataInitializer initializer = new DemoDataInitializer(
                studentRepository,
                teacherRepository,
                classroomRepository,
                scheduleRepository,
                attendanceRepository,
                tuitionRepository,
                messageRepository,
                userRepository,
                passwordEncoder
        );
        ReflectionTestUtils.setField(initializer, "seedEnabled", true);
        DefaultApplicationArguments arguments = new DefaultApplicationArguments(new String[0]);

        initializer.run(arguments);
        assertEquals(6, students.size());
        assertEquals(6, teachers.size());
        assertEquals(6, classrooms.size());
        assertEquals(240, schedules.size());
        assertEquals(6, attendance.size());
        assertEquals(5, invoices.size());
        assertEquals(3, messages.size());
        assertEquals(4, users.size());

        initializer.run(arguments);
        assertEquals(6, students.size());
        assertEquals(6, teachers.size());
        assertEquals(6, classrooms.size());
        assertEquals(240, schedules.size());
        assertEquals(6, attendance.size());
        assertEquals(5, invoices.size());
        assertEquals(3, messages.size());
        assertEquals(4, users.size());
        assertTrue(users.stream().anyMatch(user ->
                user.getUsername().equals("admin@annhien.vn")
                        && user.getRole() == RoleType.ADMIN
                        && user.getPassword().equals("encoded-demo-password")));
        assertTrue(students.stream().anyMatch(student ->
                "ngocanh@annhien.vn".equals(student.getParentEmail())
                        && student.getParentUserId() != null));
    }

    private static <T> T add(List<T> records, T record) {
        if (!records.contains(record)) {
            records.add(record);
        }
        return record;
    }
}
