package org.example.preschool.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.preschool.model.dto.DashboardSummary;
import org.example.preschool.model.dto.ParentSummary;
import org.example.preschool.model.entity.AttendanceRecord;
import org.example.preschool.model.entity.BaseEntity;
import org.example.preschool.model.entity.Classroom;
import org.example.preschool.model.entity.ScheduleActivity;
import org.example.preschool.model.entity.SchoolMessage;
import org.example.preschool.model.entity.Student;
import org.example.preschool.model.entity.Teacher;
import org.example.preschool.model.entity.TuitionInvoice;
import org.example.preschool.model.entity.User;
import org.example.preschool.model.request.AttendanceBatchRequest;
import org.example.preschool.repository.AttendanceRepository;
import org.example.preschool.repository.ClassroomRepository;
import org.example.preschool.repository.ScheduleActivityRepository;
import org.example.preschool.repository.SchoolMessageRepository;
import org.example.preschool.repository.StudentRepository;
import org.example.preschool.repository.TeacherRepository;
import org.example.preschool.repository.TuitionInvoiceRepository;
import org.example.preschool.service.PreschoolService;
import org.example.preschool.util.constant.ErrorCode;
import org.example.preschool.util.constant.RoleType;
import org.example.preschool.util.exception.ServiceException;
import org.example.preschool.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreschoolServiceImpl implements PreschoolService {

    private static final String PRESENT = "Đã đến";
    private static final String ABSENT = "Xin nghỉ";
    private static final String PENDING = "Chưa điểm danh";
    private static final Set<String> ATTENDANCE_STATUSES = Set.of(PRESENT, ABSENT, PENDING);

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final ScheduleActivityRepository scheduleActivityRepository;
    private final AttendanceRepository attendanceRepository;
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final SchoolMessageRepository schoolMessageRepository;

    @Override
    public List<Student> getStudents(User actor, String classroom) {
        return studentRepository.findAll().stream()
                .filter(student -> canReadStudent(actor, student))
                .filter(student -> !StringUtils.hasText(classroom) || classroom.equals(student.getClassroom()))
                .toList();
    }

    @Override
    public Student getStudent(User actor, String id) {
        Student student = findStudent(id);
        if (!canReadStudent(actor, student)) {
            throw forbidden();
        }
        return student;
    }

    @Override
    public List<ParentSummary> getParents(User actor) {
        requireAdmin(actor);
        Map<String, List<Student>> familyMap = studentRepository.findAll().stream()
                .filter(student -> StringUtils.hasText(student.getParent()))
                .collect(Collectors.groupingBy(Student::getParent, LinkedHashMap::new, Collectors.toList()));
        return familyMap.entrySet().stream()
                .map(entry -> new ParentSummary(
                        entry.getKey(),
                        entry.getValue().stream().map(Student::getPhone).filter(StringUtils::hasText).findFirst().orElse(""),
                        entry.getValue()
                ))
                .toList();
    }

    @Override
    public Student createStudent(User actor, Student student) {
        requireAdmin(actor);
        student.setId(StringUtil.generateId());
        student.setStatus(PENDING);
        prepareCreate(student);
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(User actor, String id, Student student) {
        requireAdmin(actor);
        Student existing = findStudent(id);
        student.setId(existing.getId());
        preserveMetadata(student, existing);
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(User actor, String id) {
        requireAdmin(actor);
        studentRepository.delete(findStudent(id));
    }

    @Override
    public List<Teacher> getTeachers(User actor) {
        requireAdmin(actor);
        return teacherRepository.findAll();
    }

    @Override
    public Teacher createTeacher(User actor, Teacher teacher) {
        requireAdmin(actor);
        teacher.setId(StringUtil.generateId());
        prepareCreate(teacher);
        return teacherRepository.save(teacher);
    }

    @Override
    public Teacher updateTeacher(User actor, String id, Teacher teacher) {
        requireAdmin(actor);
        Teacher existing = teacherRepository.findById(id)
                .orElseThrow(() -> notFound("Teacher not found"));
        teacher.setId(existing.getId());
        preserveMetadata(teacher, existing);
        return teacherRepository.save(teacher);
    }

    @Override
    public void deleteTeacher(User actor, String id) {
        requireAdmin(actor);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> notFound("Teacher not found"));
        teacherRepository.delete(teacher);
    }

    @Override
    public List<Classroom> getClassrooms(User actor) {
        requireRole(actor, RoleType.ADMIN, RoleType.SUPER_ADMIN, RoleType.TEACHER);
        return classroomRepository.findAll().stream()
                .filter(classroom -> !isTeacher(actor) || Objects.equals(classroom.getName(), actor.getClassroom()))
                .toList();
    }

    @Override
    public Classroom createClassroom(User actor, Classroom classroom) {
        requireAdmin(actor);
        classroom.setId(StringUtil.generateId());
        prepareCreate(classroom);
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom updateClassroom(User actor, String id, Classroom classroom) {
        requireAdmin(actor);
        Classroom existing = classroomRepository.findById(id)
                .orElseThrow(() -> notFound("Classroom not found"));
        classroom.setId(existing.getId());
        preserveMetadata(classroom, existing);
        return classroomRepository.save(classroom);
    }

    @Override
    public void deleteClassroom(User actor, String id) {
        requireAdmin(actor);
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> notFound("Classroom not found"));
        classroomRepository.delete(classroom);
    }

    @Override
    public List<ScheduleActivity> getSchedule(User actor, String classroom) {
        String scope = classroomScope(actor, classroom);
        return scheduleActivityRepository.findAll().stream()
                .filter(activity -> !StringUtils.hasText(scope) || scope.equals(activity.getClassroom()))
                .toList();
    }

    @Override
    public ScheduleActivity createScheduleActivity(User actor, ScheduleActivity activity) {
        requireCanManageClassroom(actor, activity.getClassroom());
        activity.setId(StringUtil.generateId());
        prepareCreate(activity);
        return scheduleActivityRepository.save(activity);
    }

    @Override
    public ScheduleActivity updateScheduleActivity(User actor, String id, ScheduleActivity activity) {
        ScheduleActivity existing = scheduleActivityRepository.findById(id)
                .orElseThrow(() -> notFound("Schedule activity not found"));
        requireCanManageClassroom(actor, existing.getClassroom());
        requireCanManageClassroom(actor, activity.getClassroom());
        activity.setId(existing.getId());
        preserveMetadata(activity, existing);
        return scheduleActivityRepository.save(activity);
    }

    @Override
    public void deleteScheduleActivity(User actor, String id) {
        ScheduleActivity existing = scheduleActivityRepository.findById(id)
                .orElseThrow(() -> notFound("Schedule activity not found"));
        requireCanManageClassroom(actor, existing.getClassroom());
        scheduleActivityRepository.delete(existing);
    }

    @Override
    public List<AttendanceRecord> getAttendance(User actor, LocalDate date, String classroom) {
        requireRole(actor, RoleType.ADMIN, RoleType.SUPER_ADMIN, RoleType.TEACHER);
        String scope = classroomScope(actor, classroom);
        List<Student> students = studentRepository.findAll().stream()
                .filter(student -> !StringUtils.hasText(scope) || scope.equals(student.getClassroom()))
                .toList();
        if (students.isEmpty()) {
            return List.of();
        }
        Map<String, AttendanceRecord> records = attendanceRepository
                .findByDateAndStudentIdIn(date, students.stream().map(Student::getId).toList())
                .stream().collect(Collectors.toMap(AttendanceRecord::getStudentId, record -> record));
        List<AttendanceRecord> result = new ArrayList<>();
        for (Student student : students) {
            AttendanceRecord record = records.get(student.getId());
            if (record == null) {
                record = new AttendanceRecord();
                record.setDate(date);
                record.setStudentId(student.getId());
                record.setStudentName(student.getName());
                record.setClassroom(student.getClassroom());
                record.setStatus(PENDING);
            }
            result.add(record);
        }
        return result;
    }

    @Override
    public List<AttendanceRecord> saveAttendance(User actor, AttendanceBatchRequest request) {
        requireRole(actor, RoleType.ADMIN, RoleType.SUPER_ADMIN, RoleType.TEACHER);
        List<AttendanceRecord> result = new ArrayList<>();
        Set<String> updatedIds = new java.util.HashSet<>();
        for (AttendanceBatchRequest.AttendanceItem item : request.records()) {
            if (!updatedIds.add(item.studentId())) {
                throw new ServiceException(ErrorCode.E400.code(), "Each student may appear only once per attendance batch");
            }
            if (!ATTENDANCE_STATUSES.contains(item.status())) {
                throw new ServiceException(ErrorCode.E400.code(), "Unsupported attendance status: " + item.status());
            }
            Student student = findStudent(item.studentId());
            requireCanManageClassroom(actor, student.getClassroom());
            AttendanceRecord record = attendanceRepository.findByDateAndStudentId(request.date(), student.getId())
                    .orElseGet(AttendanceRecord::new);
            if (record.getId() == null) {
                record.setId(StringUtil.generateId());
                prepareCreate(record);
            } else {
                prepareUpdate(record);
            }
            record.setDate(request.date());
            record.setStudentId(student.getId());
            record.setStudentName(student.getName());
            record.setClassroom(student.getClassroom());
            record.setStatus(item.status());
            record.setCheckInTime(item.checkInTime());
            student.setStatus(item.status());
            prepareUpdate(student);
            studentRepository.save(student);
            result.add(attendanceRepository.save(record));
        }
        return result;
    }

    @Override
    public List<TuitionInvoice> getTuition(User actor, String month, String classroom) {
        requireRole(actor, RoleType.ADMIN, RoleType.SUPER_ADMIN, RoleType.PARENT, RoleType.CUSTOMER);
        Set<String> studentIds = linkedStudentIds(actor);
        return tuitionInvoiceRepository.findAll().stream()
                .filter(invoice -> !StringUtils.hasText(month) || month.equals(invoice.getMonth()))
                .filter(invoice -> !StringUtils.hasText(classroom) || classroom.equals(invoice.getRoom()))
                .filter(invoice -> isAdmin(actor) || studentIds.contains(invoice.getStudentId()))
                .toList();
    }

    @Override
    public TuitionInvoice createInvoice(User actor, TuitionInvoice invoice) {
        requireAdmin(actor);
        Student student = findStudent(invoice.getStudentId());
        invoice.setName(student.getName());
        invoice.setRoom(student.getClassroom());
        invoice.setId(StringUtil.generateId());
        prepareCreate(invoice);
        return tuitionInvoiceRepository.save(invoice);
    }

    @Override
    public TuitionInvoice updateInvoice(User actor, String id, TuitionInvoice invoice) {
        requireAdmin(actor);
        TuitionInvoice existing = tuitionInvoiceRepository.findById(id)
                .orElseThrow(() -> notFound("Tuition invoice not found"));
        Student student = findStudent(invoice.getStudentId());
        invoice.setId(existing.getId());
        invoice.setName(student.getName());
        invoice.setRoom(student.getClassroom());
        preserveMetadata(invoice, existing);
        invoice.setReminderSentAt(existing.getReminderSentAt());
        return tuitionInvoiceRepository.save(invoice);
    }

    @Override
    public TuitionInvoice sendInvoiceReminder(User actor, String id) {
        requireAdmin(actor);
        TuitionInvoice invoice = tuitionInvoiceRepository.findById(id)
                .orElseThrow(() -> notFound("Tuition invoice not found"));
        Student student = findStudent(invoice.getStudentId());
        SchoolMessage reminder = new SchoolMessage();
        reminder.setTitle("Nhắc nhở học phí tháng " + invoice.getMonth());
        reminder.setBody("Nhà trường kính nhờ ba mẹ hoàn tất học phí cho "
                + invoice.getName() + " trước ngày " + invoice.getDue() + ".");
        reminder.setAudience("Phụ huynh");
        reminder.setClassroom(invoice.getRoom());
        reminder.setTag("HỌC PHÍ");
        reminder.setInitials("AN");
        reminder.setTone("yellow");
        if (StringUtils.hasText(student.getParentUserId())) {
            reminder.setRecipientIds(List.of(student.getParentUserId()));
        }
        reminder.setId(StringUtil.generateId());
        prepareCreate(reminder);
        reminder.setDate(reminder.getCreatedAt());
        schoolMessageRepository.save(reminder);
        invoice.setReminderSentAt(LocalDateTime.now());
        prepareUpdate(invoice);
        return tuitionInvoiceRepository.save(invoice);
    }

    @Override
    public List<TuitionInvoice> sendInvoiceReminders(User actor, String month) {
        requireAdmin(actor);
        return tuitionInvoiceRepository.findAll().stream()
                .filter(invoice -> !StringUtils.hasText(month) || month.equals(invoice.getMonth()))
                .filter(invoice -> !"Đã thanh toán".equals(invoice.getStatus()))
                .map(invoice -> sendInvoiceReminder(actor, invoice.getId()))
                .toList();
    }

    @Override
    public List<SchoolMessage> getMessages(User actor) {
        Set<String> classrooms = classroomsFor(actor);
        return schoolMessageRepository.findAll().stream()
                .filter(message -> isAdmin(actor)
                        || message.getRecipientIds() == null
                        || message.getRecipientIds().isEmpty()
                        || message.getRecipientIds().contains(actor.getUserId()))
                .filter(message -> !StringUtils.hasText(message.getClassroom())
                        || isAdmin(actor)
                        || classrooms.contains(message.getClassroom()))
                .sorted(Comparator.comparing(
                        SchoolMessage::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    @Override
    public SchoolMessage createMessage(User actor, SchoolMessage message) {
        requireRole(actor, RoleType.ADMIN, RoleType.SUPER_ADMIN, RoleType.TEACHER);
        if (isTeacher(actor)) {
            requireCanManageClassroom(actor, message.getClassroom());
        }
        message.setId(StringUtil.generateId());
        prepareCreate(message);
        message.setDate(message.getCreatedAt());
        return schoolMessageRepository.save(message);
    }

    @Override
    public DashboardSummary getDashboardSummary(User actor, LocalDate date, String month) {
        requireAdmin(actor);
        List<Student> students = studentRepository.findAll();
        List<AttendanceRecord> attendance = attendanceRepository.findByDate(date);
        long present = attendance.stream().filter(record -> PRESENT.equals(record.getStatus())).count();
        if (attendance.isEmpty()) {
            present = students.stream().filter(student -> PRESENT.equals(student.getStatus())).count();
        }
        List<TuitionInvoice> invoices = tuitionRepositoryForMonth(month);
        BigDecimal paid = invoices.stream()
                .filter(invoice -> "Đã thanh toán".equals(invoice.getStatus()))
                .map(TuitionInvoice::getAmount).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = invoices.stream()
                .filter(invoice -> !"Đã thanh toán".equals(invoice.getStatus()))
                .map(TuitionInvoice::getAmount).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new DashboardSummary(
                students.size(),
                present,
                classroomRepository.count(),
                paid,
                outstanding,
                date
        );
    }

    private List<TuitionInvoice> tuitionRepositoryForMonth(String month) {
        return tuitionInvoiceRepository.findAll().stream()
                .filter(invoice -> !StringUtils.hasText(month) || month.equals(invoice.getMonth()))
                .toList();
    }

    private Student findStudent(String id) {
        return studentRepository.findById(id).orElseThrow(() -> notFound("Student not found"));
    }

    private boolean canReadStudent(User actor, Student student) {
        if (isAdmin(actor)) {
            return true;
        }
        if (isTeacher(actor)) {
            return Objects.equals(student.getClassroom(), actor.getClassroom());
        }
        return linkedStudentIds(actor).contains(student.getId());
    }

    private Set<String> linkedStudentIds(User actor) {
        Set<String> ids = studentRepository.findAll().stream()
                .filter(student -> (StringUtils.hasText(actor.getUserId())
                        && actor.getUserId().equals(student.getParentUserId()))
                        || (StringUtils.hasText(actor.getUsername())
                        && actor.getUsername().equalsIgnoreCase(student.getParentEmail())))
                .map(Student::getId)
                .collect(Collectors.toSet());
        if (StringUtils.hasText(actor.getStudentId())) {
            ids.add(actor.getStudentId());
        }
        return ids;
    }

    private Set<String> classroomsFor(User actor) {
        if (isAdmin(actor)) {
            return Set.of();
        }
        if (isTeacher(actor) && StringUtils.hasText(actor.getClassroom())) {
            return Set.of(actor.getClassroom());
        }
        Set<String> linkedIds = linkedStudentIds(actor);
        return studentRepository.findAll().stream()
                .filter(student -> linkedIds.contains(student.getId()))
                .map(Student::getClassroom)
                .collect(Collectors.toSet());
    }

    private String classroomScope(User actor, String requestedClassroom) {
        requireRole(
                actor,
                RoleType.ADMIN,
                RoleType.SUPER_ADMIN,
                RoleType.TEACHER,
                RoleType.PARENT,
                RoleType.CUSTOMER,
                RoleType.STUDENT
        );
        if (isAdmin(actor)) {
            return requestedClassroom;
        }
        if (isTeacher(actor)) {
            if (StringUtils.hasText(requestedClassroom) && !requestedClassroom.equals(actor.getClassroom())) {
                throw forbidden();
            }
            return actor.getClassroom();
        }
        Set<String> classrooms = classroomsFor(actor);
        if (StringUtils.hasText(requestedClassroom) && !classrooms.contains(requestedClassroom)) {
            throw forbidden();
        }
        return StringUtils.hasText(requestedClassroom)
                ? requestedClassroom
                : classrooms.stream().findFirst().orElse("");
    }

    private void requireCanManageClassroom(User actor, String classroom) {
        requireRole(actor, RoleType.ADMIN, RoleType.SUPER_ADMIN, RoleType.TEACHER);
        if (isTeacher(actor) && !Objects.equals(actor.getClassroom(), classroom)) {
            throw forbidden();
        }
    }

    private void requireAdmin(User actor) {
        requireRole(actor, RoleType.ADMIN, RoleType.SUPER_ADMIN);
    }

    private void requireRole(User actor, RoleType... roles) {
        if (actor == null || actor.getRole() == null) {
            throw forbidden();
        }
        for (RoleType role : roles) {
            if (actor.getRole() == role) {
                return;
            }
        }
        throw forbidden();
    }

    private boolean isAdmin(User actor) {
        return actor != null && (actor.getRole() == RoleType.ADMIN || actor.getRole() == RoleType.SUPER_ADMIN);
    }

    private boolean isTeacher(User actor) {
        return actor != null && actor.getRole() == RoleType.TEACHER;
    }

    private void prepareCreate(BaseEntity entity) {
        String now = LocalDateTime.now().toString();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        if (!StringUtils.hasText(entity.getStatus())) {
            entity.setStatus("ACTIVE");
        }
    }

    private void prepareUpdate(BaseEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now().toString());
    }

    private void preserveMetadata(BaseEntity updated, BaseEntity existing) {
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(LocalDateTime.now().toString());
        updated.setStatus(existing.getStatus());
        updated.setSequence(existing.getSequence());
    }

    private ServiceException notFound(String message) {
        return new ServiceException(ErrorCode.E404.code(), message);
    }

    private ServiceException forbidden() {
        return new ServiceException(ErrorCode.E403.code(), "You do not have permission to access this resource");
    }
}
