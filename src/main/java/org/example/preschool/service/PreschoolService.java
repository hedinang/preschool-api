package org.example.preschool.service;

import org.example.preschool.model.dto.DashboardSummary;
import org.example.preschool.model.dto.ParentSummary;
import org.example.preschool.model.entity.AttendanceRecord;
import org.example.preschool.model.entity.Classroom;
import org.example.preschool.model.entity.ScheduleActivity;
import org.example.preschool.model.entity.SchoolMessage;
import org.example.preschool.model.entity.Student;
import org.example.preschool.model.entity.Teacher;
import org.example.preschool.model.entity.TuitionInvoice;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.AttendanceBatchRequest;

import java.time.LocalDate;
import java.util.List;

public interface PreschoolService {

    List<Student> getStudents(User actor, String classroom);

    Student getStudent(User actor, String id);

    List<ParentSummary> getParents(User actor);

    Student createStudent(User actor, Student student);

    Student updateStudent(User actor, String id, Student student);

    void deleteStudent(User actor, String id);

    List<Teacher> getTeachers(User actor);

    Teacher createTeacher(User actor, Teacher teacher);

    Teacher updateTeacher(User actor, String id, Teacher teacher);

    void deleteTeacher(User actor, String id);

    List<Classroom> getClassrooms(User actor);

    Classroom createClassroom(User actor, Classroom classroom);

    Classroom updateClassroom(User actor, String id, Classroom classroom);

    void deleteClassroom(User actor, String id);

    List<ScheduleActivity> getSchedule(User actor, String classroom);

    ScheduleActivity createScheduleActivity(User actor, ScheduleActivity activity);

    ScheduleActivity updateScheduleActivity(User actor, String id, ScheduleActivity activity);

    void deleteScheduleActivity(User actor, String id);

    List<AttendanceRecord> getAttendance(User actor, LocalDate date, String classroom);

    List<AttendanceRecord> saveAttendance(User actor, AttendanceBatchRequest request);

    List<TuitionInvoice> getTuition(User actor, String month, String classroom);

    TuitionInvoice createInvoice(User actor, TuitionInvoice invoice);

    TuitionInvoice updateInvoice(User actor, String id, TuitionInvoice invoice);

    TuitionInvoice sendInvoiceReminder(User actor, String id);

    List<TuitionInvoice> sendInvoiceReminders(User actor, String month);

    List<SchoolMessage> getMessages(User actor);

    SchoolMessage createMessage(User actor, SchoolMessage message);

    DashboardSummary getDashboardSummary(User actor, LocalDate date, String month);
}
