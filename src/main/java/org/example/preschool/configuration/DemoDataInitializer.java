package org.example.preschool.configuration;

import lombok.RequiredArgsConstructor;
import org.example.preschool.model.entity.AttendanceRecord;
import org.example.preschool.model.entity.BaseEntity;
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
import org.example.preschool.util.StringUtil;
import org.example.preschool.util.constant.RoleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {

    private static final String DEMO_PASSWORD = "123456";
    private static final List<ClassroomSeed> CLASSROOMS = List.of(
            new ClassroomSeed("Mầm 1", "3 – 4 tuổi", 18, "Mai Phương", "07:30 – 16:30", "🌱", "class-mint"),
            new ClassroomSeed("Mầm 2", "3 – 4 tuổi", 18, "Thu Hằng", "07:30 – 16:30", "🌼", "class-peach"),
            new ClassroomSeed("Chồi 1", "4 – 5 tuổi", 18, "Bảo Trân", "07:30 – 16:30", "🌷", "class-pink"),
            new ClassroomSeed("Chồi 2", "4 – 5 tuổi", 18, "Minh Thư", "07:30 – 16:30", "🦋", "class-lilac"),
            new ClassroomSeed("Lá 1", "5 – 6 tuổi", 20, "Ngọc Lan", "07:30 – 16:30", "🍃", "class-blue"),
            new ClassroomSeed("Lá 2", "5 – 6 tuổi", 20, "Thanh Vân", "07:30 – 16:30", "🌻", "class-yellow")
    );
    private static final List<StudentSeed> STUDENTS = createStudentSeeds();
    private static final List<TeacherSeed> TEACHERS = List.of(
            new TeacherSeed("Mai Phương", "Giáo viên chủ nhiệm", "Mầm 1", "0901 234 567", "maiphuong@annhien.vn", "6 năm", "MP", "mint"),
            new TeacherSeed("Thu Hằng", "Giáo viên chủ nhiệm", "Mầm 2", "0902 345 678", "thuhang@annhien.vn", "5 năm", "TH", "peach"),
            new TeacherSeed("Bảo Trân", "Giáo viên chủ nhiệm", "Chồi 1", "0903 456 789", "baotran@annhien.vn", "8 năm", "BT", "lilac"),
            new TeacherSeed("Minh Thư", "Giáo viên chủ nhiệm", "Chồi 2", "0904 567 890", "minhthu@annhien.vn", "4 năm", "MT", "pink"),
            new TeacherSeed("Ngọc Lan", "Giáo viên chủ nhiệm", "Lá 1", "0905 678 901", "ngoclan@annhien.vn", "7 năm", "NL", "yellow"),
            new TeacherSeed("Thanh Vân", "Giáo viên chủ nhiệm", "Lá 2", "0906 789 012", "thanhvan@annhien.vn", "3 năm", "TV", "blue")
    );
    private static final String[] DAYS = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu"};
    private static final String[] START_TIMES = {"07:30", "08:00", "09:00", "09:45", "10:15", "11:00", "14:00", "15:00"};
    private static final String[] END_TIMES = {"08:00", "09:00", "09:45", "10:15", "11:00", "14:00", "15:00", "16:30"};
    private static final String[][] SCHEDULE_TITLES = {
            {"Đón bé & thể dục sáng", "Làm quen chữ cái", "Vận động ngoài trời", "Ăn nhẹ", "Khám phá thế giới", "Ăn trưa & ngủ trưa", "Tạo hình: chiếc lá", "Chơi tự do, trả bé"},
            {"Đón bé & thể dục sáng", "Nhận biết màu sắc", "Âm nhạc vui nhộn", "Ăn nhẹ", "Kể chuyện: Thỏ con", "Ăn trưa & ngủ trưa", "Trò chơi vận động", "Chơi tự do, trả bé"},
            {"Đón bé & thể dục sáng", "Bé tập đếm số", "Góc chơi khám phá", "Ăn nhẹ", "Làm quen tiếng Anh", "Ăn trưa & ngủ trưa", "Vẽ tranh cùng bé", "Chơi tự do, trả bé"},
            {"Đón bé & thể dục sáng", "Thơ: Bé đến lớp", "Vườn rau của bé", "Ăn nhẹ", "Khám phá khoa học", "Ăn trưa & ngủ trưa", "Xé dán giấy màu", "Chơi tự do, trả bé"},
            {"Đón bé & thể dục sáng", "Ôn tập tuần này", "Trò chơi cùng bạn", "Ăn nhẹ", "Bé kể chuyện tuần qua", "Ăn trưa & ngủ trưa", "Vũ điệu cuối tuần", "Chơi tự do, trả bé"}
    };
    private static final String[][] SCHEDULE_KINDS = {
            {"care", "learn", "play", "care", "learn", "care", "art", "play"},
            {"care", "learn", "art", "care", "learn", "care", "play", "play"},
            {"care", "learn", "play", "care", "learn", "care", "art", "play"},
            {"care", "learn", "play", "care", "learn", "care", "art", "play"},
            {"care", "learn", "play", "care", "learn", "care", "art", "play"}
    };

    private static List<StudentSeed> createStudentSeeds() {
        List<StudentSeed> seeds = new ArrayList<>(List.of(
                new StudentSeed("Nguyễn Minh Khôi", "Nam", 4, "Mầm 1", "Chị Ngọc Anh", "0912 345 678", "Đã đến", "MK", "peach", "07:42"),
                new StudentSeed("Trần Bảo Ngọc", "Nữ", 3, "Chồi 2", "Anh Tuấn Minh", "0987 123 456", "Đã đến", "BN", "lilac", "07:56"),
                new StudentSeed("Lê Gia Hân", "Nữ", 5, "Lá 1", "Chị Thanh Hà", "0903 456 789", "Đã đến", "GH", "mint", "08:02"),
                new StudentSeed("Phạm Đức Anh", "Nam", 4, "Mầm 2", "Anh Hoàng Nam", "0934 567 890", "Đã đến", "ĐA", "blue", "07:38"),
                new StudentSeed("Vũ Khánh Linh", "Nữ", 3, "Chồi 1", "Chị Mỹ Linh", "0978 234 567", "Xin nghỉ", "KL", "yellow", ""),
                new StudentSeed("Đỗ Nhật Nam", "Nam", 5, "Lá 2", "Anh Nhật Quang", "0918 765 432", "Đã đến", "NN", "pink", "07:48")
        ));
        String[] classrooms = CLASSROOMS.stream().map(ClassroomSeed::name).toArray(String[]::new);
        for (int index = 1; index <= 54; index++) {
            String suffix = String.format("%02d", index);
            seeds.add(new StudentSeed(
                    "Học sinh demo " + suffix,
                    index % 2 == 0 ? "Nữ" : "Nam",
                    3 + index % 3,
                    classrooms[(index - 1) % classrooms.length],
                    "Phụ huynh demo " + suffix,
                    String.format("091%1d %03d %03d", index % 10, 100 + index, 200 + index),
                    index % 7 == 0 ? "Xin nghỉ" : "Đã đến",
                    "HS" + suffix,
                    new String[]{"peach", "lilac", "mint", "blue", "yellow", "pink"}[(index - 1) % 6],
                    index % 7 == 0 ? "" : String.format("07:%02d", 30 + index % 30)
            ));
        }
        return List.copyOf(seeds);
    }

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final ScheduleActivityRepository scheduleActivityRepository;
    private final AttendanceRepository attendanceRepository;
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final SchoolMessageRepository schoolMessageRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${preschool.seed-demo-data:true}")
    private boolean seedEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            return;
        }

        String timestamp = LocalDateTime.now().toString();
        seedClassrooms(timestamp);
        Map<String, Student> students = seedStudents(timestamp);
        seedTeachers(timestamp);
        Map<String, User> users = seedUsers(timestamp, students);
        linkParentToStudent(students.get("Nguyễn Minh Khôi"), users.get("ngocanh@annhien.vn"));
        seedSchedule(timestamp);
        seedAttendance(timestamp, students);
        seedTuition(timestamp, students);
        seedMessages(timestamp);
    }

    private void seedClassrooms(String timestamp) {
        Set<String> existing = new HashSet<>();
        for (Classroom classroom : classroomRepository.findAll()) {
            existing.add(classroom.getName());
        }
        for (ClassroomSeed seed : CLASSROOMS) {
            if (!existing.add(seed.name())) {
                continue;
            }
            Classroom classroom = new Classroom();
            classroom.setId(StringUtil.generateId());
            classroom.setName(seed.name());
            classroom.setAge(seed.age());
            classroom.setCapacity(seed.capacity());
            classroom.setTeacher(seed.teacher());
            classroom.setTime(seed.time());
            classroom.setIcon(seed.icon());
            classroom.setTone(seed.tone());
            setTimestamps(classroom, timestamp);
            classroomRepository.save(classroom);
        }
    }

    private Map<String, Student> seedStudents(String timestamp) {
        Map<String, Student> result = new HashMap<>();
        for (Student student : studentRepository.findAll()) {
            result.putIfAbsent(student.getName(), student);
        }
        for (StudentSeed seed : STUDENTS) {
            if (result.containsKey(seed.name())) {
                continue;
            }
            Student student = new Student();
            student.setId(StringUtil.generateId());
            student.setName(seed.name());
            student.setGender(seed.gender());
            student.setAge(seed.age());
            student.setClassroom(seed.classroom());
            student.setParent(seed.parent());
            student.setPhone(seed.phone());
            student.setAvatar(seed.avatar());
            student.setColor(seed.color());
            student.setStatus(seed.status());
            setTimestamps(student, timestamp);
            result.put(seed.name(), studentRepository.save(student));
        }
        return result;
    }

    private void seedTeachers(String timestamp) {
        Set<String> existingEmails = new HashSet<>();
        for (Teacher teacher : teacherRepository.findAll()) {
            if (teacher.getEmail() != null) {
                existingEmails.add(teacher.getEmail().toLowerCase());
            }
        }
        List<TeacherSeed> allTeacherSeeds = new ArrayList<>(TEACHERS);
        for (int index = 1; index <= 18; index++) {
            String suffix = String.format("%02d", index);
            allTeacherSeeds.add(new TeacherSeed(
                    "Giáo viên demo " + suffix,
                    index % 3 == 0 ? "Giáo viên bộ môn" : "Trợ giảng",
                    CLASSROOMS.get((index - 1) % CLASSROOMS.size()).name(),
                    String.format("092%1d %03d %03d", index % 10, 300 + index, 400 + index),
                    "giaovien.demo." + suffix + "@annhien.vn",
                    (2 + index % 9) + " năm",
                    "GV" + suffix,
                    new String[]{"mint", "peach", "lilac", "pink", "yellow", "blue"}[(index - 1) % 6]
            ));
        }
        for (TeacherSeed seed : allTeacherSeeds) {
            if (existingEmails.contains(seed.email().toLowerCase())) {
                continue;
            }
            Teacher teacher = new Teacher();
            teacher.setId(StringUtil.generateId());
            teacher.setName(seed.name());
            teacher.setTitle(seed.title());
            teacher.setClassroom(seed.classroom());
            teacher.setPhone(seed.phone());
            teacher.setEmail(seed.email());
            teacher.setExperience(seed.experience());
            teacher.setAvatar(seed.avatar());
            teacher.setColor(seed.color());
            setTimestamps(teacher, timestamp);
            teacherRepository.save(teacher);
            existingEmails.add(seed.email().toLowerCase());
        }
    }

    private Map<String, User> seedUsers(String timestamp, Map<String, Student> students) {
        Map<String, User> result = new HashMap<>();
        List<UserSeed> seeds = List.of(
                new UserSeed("admin@annhien.vn", "Lan Anh", RoleType.ADMIN, "Quản trị viên", null, null, "LA", "peach"),
                new UserSeed("maiphuong@annhien.vn", "Cô Mai Phương", RoleType.TEACHER, "Giáo viên · Mầm 1", "Mầm 1", null, "MP", "mint"),
                new UserSeed("ngocanh@annhien.vn", "Chị Ngọc Anh", RoleType.PARENT, "Phụ huynh", null, "Nguyễn Minh Khôi", "NA", "lilac"),
                new UserSeed("minhkhoi@annhien.vn", "Nguyễn Minh Khôi", RoleType.STUDENT, "Học sinh · Mầm 1", "Mầm 1", "Nguyễn Minh Khôi", "MK", "peach")
        );
        for (User user : userRepository.findAll()) {
            if (user.getUsername() != null) {
                result.put(user.getUsername().toLowerCase(), user);
            }
        }
        for (UserSeed seed : seeds) {
            String username = seed.username().toLowerCase();
            if (result.containsKey(username)) {
                continue;
            }
            User user = new User();
            user.setId(StringUtil.generateId());
            user.setUserId(StringUtil.generateId());
            user.setUsername(seed.username());
            user.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
            user.setRole(seed.role());
            user.setDisplayName(seed.displayName());
            user.setTitle(seed.title());
            user.setClassroom(seed.classroom());
            user.setAvatar(seed.avatar());
            user.setColor(seed.color());
            user.setStatus("ACTIVE");
            Student linkedStudent = seed.studentName() == null ? null : students.get(seed.studentName());
            if (linkedStudent != null) {
                user.setStudentId(linkedStudent.getId());
            }
            setTimestamps(user, timestamp);
            result.put(username, userRepository.save(user));
        }
        return result;
    }

    private void linkParentToStudent(Student student, User parent) {
        if (student == null || parent == null) {
            return;
        }
        boolean changed = false;
        if (student.getParentEmail() == null || student.getParentEmail().isBlank()) {
            student.setParentEmail(parent.getUsername());
            changed = true;
        }
        if ((student.getParentUserId() == null || student.getParentUserId().isBlank())
                && parent.getUserId() != null && !parent.getUserId().isBlank()) {
            student.setParentUserId(parent.getUserId());
            changed = true;
        }
        if (changed) {
            student.setUpdatedAt(LocalDateTime.now().toString());
            studentRepository.save(student);
        }
    }

    private void seedSchedule(String timestamp) {
        Set<String> existing = new HashSet<>();
        for (ScheduleActivity activity : scheduleActivityRepository.findAll()) {
            existing.add(scheduleKey(activity.getClassroom(), activity.getDayOfWeek(), activity.getStartTime(), activity.getTitle()));
        }
        for (ClassroomSeed classroomSeed : CLASSROOMS) {
            String classroom = classroomSeed.name();
            for (int day = 0; day < DAYS.length; day++) {
                for (int slot = 0; slot < START_TIMES.length; slot++) {
                    String title = SCHEDULE_TITLES[day][slot];
                    String key = scheduleKey(classroom, DAYS[day], START_TIMES[slot], title);
                    if (!existing.add(key)) {
                        continue;
                    }
                    ScheduleActivity activity = new ScheduleActivity();
                    activity.setId(StringUtil.generateId());
                    activity.setClassroom(classroom);
                    activity.setDayOfWeek(DAYS[day]);
                    activity.setStartTime(START_TIMES[slot]);
                    activity.setEndTime(END_TIMES[slot]);
                    activity.setTitle(title);
                    activity.setKind(SCHEDULE_KINDS[day][slot]);
                    setTimestamps(activity, timestamp);
                    scheduleActivityRepository.save(activity);
                }
            }
        }
    }

    private void seedAttendance(String timestamp, Map<String, Student> students) {
        LocalDate today = LocalDate.now();
        Set<String> existing = new HashSet<>();
        for (AttendanceRecord record : attendanceRepository.findByDate(today)) {
            existing.add(record.getStudentId());
        }
        for (Student student : students.values()) {
            if (student == null || !existing.add(student.getId())) {
                continue;
            }
            AttendanceRecord record = new AttendanceRecord();
            record.setId(StringUtil.generateId());
            record.setDate(today);
            record.setStudentId(student.getId());
            record.setStudentName(student.getName());
            record.setClassroom(student.getClassroom());
            record.setStatus(student.getStatus());
            record.setCheckInTime("Đã đến".equals(student.getStatus())
                    ? String.format("07:%02d", 30 + existing.size() % 30)
                    : "—");
            setTimestamps(record, timestamp);
            attendanceRepository.save(record);
        }
    }

    private void seedTuition(String timestamp, Map<String, Student> students) {
        LocalDate today = LocalDate.now();
        String month = today.toString().substring(0, 7);
        LocalDate due = today.withDayOfMonth(5);
        Set<String> existing = new HashSet<>();
        for (TuitionInvoice invoice : tuitionInvoiceRepository.findAll()) {
            existing.add(invoice.getStudentId() + "|" + invoice.getMonth());
        }
        List<InvoiceSeed> initialSeeds = List.of(
                new InvoiceSeed("Nguyễn Minh Khôi", new BigDecimal("3500000"), "Đã thanh toán"),
                new InvoiceSeed("Trần Bảo Ngọc", new BigDecimal("3800000"), "Chờ thanh toán"),
                new InvoiceSeed("Lê Gia Hân", new BigDecimal("4000000"), "Đã thanh toán"),
                new InvoiceSeed("Phạm Đức Anh", new BigDecimal("3500000"), "Quá hạn"),
                new InvoiceSeed("Vũ Khánh Linh", new BigDecimal("3800000"), "Chờ thanh toán")
        );
        for (Student student : students.values()) {
            if (student == null || !existing.add(student.getId() + "|" + month)) {
                continue;
            }
            InvoiceSeed seed = initialSeeds.stream()
                    .filter(item -> item.studentName().equals(student.getName()))
                    .findFirst()
                    .orElse(new InvoiceSeed(
                            student.getName(),
                            BigDecimal.valueOf(3_200_000L + student.getAge() * 100_000L),
                            "Đã thanh toán".equals(student.getStatus()) ? "Đã thanh toán" : "Chờ thanh toán"
                    ));
            TuitionInvoice invoice = new TuitionInvoice();
            invoice.setId(StringUtil.generateId());
            invoice.setStudentId(student.getId());
            invoice.setName(student.getName());
            invoice.setRoom(student.getClassroom());
            invoice.setAmount(seed.amount());
            invoice.setDue(due);
            invoice.setMonth(month);
            invoice.setStatus(seed.status());
            invoice.setAvatar(student.getAvatar());
            invoice.setColor(student.getColor());
            setTimestamps(invoice, timestamp);
            tuitionInvoiceRepository.save(invoice);
        }
    }

    private void seedMessages(String timestamp) {
        Set<String> existingTitles = new HashSet<>();
        for (SchoolMessage message : schoolMessageRepository.findAll()) {
            existingTitles.add(message.getTitle());
        }
        List<MessageSeed> seeds = new ArrayList<>(List.of(
                new MessageSeed(
                        "Lịch hoạt động ngoài trời tuần này",
                        "Các bé sẽ tham gia hoạt động khám phá thiên nhiên vào sáng thứ Sáu. Ba mẹ nhớ chuẩn bị nón và bình nước cho bé nhé!",
                        "Tất cả phụ huynh", "HOẠT ĐỘNG", "AN", "peach", 0
                ),
                new MessageSeed(
                        "Nhắc nhở học phí tháng 11",
                        "Nhà trường kính nhờ ba mẹ hoàn tất học phí tháng 11 trước ngày 05/11. Cảm ơn ba mẹ đã đồng hành cùng An Nhiên.",
                        "Phụ huynh các lớp", "HỌC PHÍ", "💛", "yellow", 1
                ),
                new MessageSeed(
                        "Thực đơn dinh dưỡng tuần 2",
                        "Thực đơn tuần mới đã được cập nhật. Ba mẹ có thể xem chi tiết bữa ăn của các bé tại bảng tin lớp.",
                        "Tất cả phụ huynh", "THỰC ĐƠN", "🥗", "mint", 2
                )
        ));
        for (int index = 1; index <= 24; index++) {
            seeds.add(new MessageSeed(
                    "Thông báo demo " + String.format("%02d", index),
                    "Nội dung thông báo mẫu số " + index + " từ Trường Mầm non An Nhiên.",
                    "Tất cả phụ huynh",
                    index % 2 == 0 ? "HOẠT ĐỘNG" : "NHÀ TRƯỜNG",
                    "AN",
                    index % 2 == 0 ? "mint" : "peach",
                    index
            ));
        }
        LocalDateTime now = LocalDateTime.now();
        for (MessageSeed seed : seeds) {
            if (!existingTitles.add(seed.title())) {
                continue;
            }
            SchoolMessage message = new SchoolMessage();
            message.setId(StringUtil.generateId());
            message.setTitle(seed.title());
            message.setBody(seed.body());
            message.setAudience(seed.audience());
            message.setTag(seed.tag());
            message.setInitials(seed.initials());
            message.setTone(seed.tone());
            message.setDate(now.minusDays(seed.daysAgo()).toString());
            setTimestamps(message, timestamp);
            schoolMessageRepository.save(message);
        }
    }

    private static String scheduleKey(String classroom, String day, String startTime, String title) {
        return classroom + "|" + day + "|" + startTime + "|" + title;
    }

    private static void setTimestamps(BaseEntity entity, String timestamp) {
        entity.setCreatedAt(timestamp);
        entity.setUpdatedAt(timestamp);
    }

    private record ClassroomSeed(String name, String age, int capacity, String teacher, String time, String icon, String tone) {
    }

    private record StudentSeed(String name, String gender, int age, String classroom, String parent, String phone, String status, String avatar, String color, String checkInTime) {
    }

    private record TeacherSeed(String name, String title, String classroom, String phone, String email, String experience, String avatar, String color) {
    }

    private record UserSeed(String username, String displayName, RoleType role, String title, String classroom, String studentName, String avatar, String color) {
    }

    private record InvoiceSeed(String studentName, BigDecimal amount, String status) {
    }

    private record MessageSeed(String title, String body, String audience, String tag, String initials, String tone, int daysAgo) {
    }
}
