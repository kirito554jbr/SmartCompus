package org.example.smartcompus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.example.smartcompus.model.Course;
import org.example.smartcompus.model.Major;
import org.example.smartcompus.model.Room;
import org.example.smartcompus.model.Student;
import org.example.smartcompus.model.Teacher;
import org.example.smartcompus.model.User;
import org.example.smartcompus.model.enums.UserRole;
import org.example.smartcompus.repository.CourseRepository;
import org.example.smartcompus.repository.MajorRepository;
import org.example.smartcompus.repository.RoomRepository;
import org.example.smartcompus.repository.StudentRepository;
import org.example.smartcompus.repository.TeacherRepository;
import org.example.smartcompus.repository.TimetableRepository;
import org.example.smartcompus.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class SmartCompusApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    @org.junit.jupiter.api.BeforeEach
    void cleanDatabase() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        timetableRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
        roomRepository.deleteAll();
        majorRepository.deleteAll();
    }

    @Test
    void login_WithValidCredentials_ReturnsJwtAndRefreshToken() throws Exception {
        createEnabledUser("admin-login@test.com", "pass123", UserRole.ROLE_ADMIN);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin-login@test.com",
                                  "password": "pass123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin-login@test.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void accessAdminEndpoint_WithStudentRole_Returns403() throws Exception {
        Major major = new Major();
        major.setName("Computer Science");
        major = majorRepository.save(major);

        Student student = new Student();
        student.setFirstName("Stu");
        student.setLastName("Dent");
        student.setEmail("student-forbidden@test.com");
        student.setPassword(passwordEncoder.encode("pass123"));
        student.setEnabeld(true);
        student.setRole(UserRole.ROLE_STUDENT);
        student.setStudentNumber("STU-FORBIDDEN-001");
        student.setMajor(major);
        studentRepository.save(student);

        String studentToken = loginAndGetAccessToken("student-forbidden@test.com", "pass123");

        mockMvc.perform(get("/api/users/role/ROLE_ADMIN")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStudentMajor_WithValidStudentNumberAndMajor_UpdatesMajorAndReturns200() throws Exception {
        createEnabledUser("admin-major@test.com", "pass123", UserRole.ROLE_ADMIN);
        String adminToken = loginAndGetAccessToken("admin-major@test.com", "pass123");

        Major oldMajor = new Major();
        oldMajor.setName("Informatics");
        oldMajor = majorRepository.save(oldMajor);

        Major newMajor = new Major();
        newMajor.setName("Networks");
        newMajor = majorRepository.save(newMajor);

        Student student = new Student();
        student.setFirstName("Ali");
        student.setLastName("Ben");
        student.setEmail("student-major@test.com");
        student.setPassword(passwordEncoder.encode("pass123"));
        student.setEnabeld(true);
        student.setRole(UserRole.ROLE_STUDENT);
        student.setStudentNumber("STU-2024-001");
        student.setMajor(oldMajor);
        student = studentRepository.save(student);

        mockMvc.perform(put("/api/students/number/STU-2024-001/major")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"role\":\"ROLE_STUDENT\"," +
                                "\"majorId\":" + newMajor.getIdMajor() +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentNumber").value("STU-2024-001"))
                .andExpect(jsonPath("$.major").value("Networks"));

        Student updatedStudent = studentRepository.findById(student.getIdUser()).orElseThrow();
        assertThat(updatedStudent.getMajor().getIdMajor()).isEqualTo(newMajor.getIdMajor());
    }

    @Test
    void createTimetable_WithRoomConflictSameDateAndTime_Returns409() throws Exception {
        createEnabledUser("admin-timetable-conflict@test.com", "pass123", UserRole.ROLE_ADMIN);
        String adminToken = loginAndGetAccessToken("admin-timetable-conflict@test.com", "pass123");

        Teacher teacher = createTeacher("teacher-conflict@test.com");
        Course course = createCourse(teacher, "Data Structures", "CS201");
        Room room = createRoom("Room-Conflict-101");

        String firstPayload = "{" +
                "\"date\":\"2026-05-23\"," +
                "\"day\":\"MONDAY\"," +
                "\"startTime\":\"09:00:00\"," +
                "\"endTime\":\"11:00:00\"," +
                "\"courseId\":" + course.getIdCourse() + "," +
                "\"roomId\":" + room.getId() +
                "}";

        String conflictPayload = "{" +
                "\"date\":\"2026-05-23\"," +
                "\"day\":\"MONDAY\"," +
                "\"startTime\":\"10:00:00\"," +
                "\"endTime\":\"12:00:00\"," +
                "\"courseId\":" + course.getIdCourse() + "," +
                "\"roomId\":" + room.getId() +
                "}";

        mockMvc.perform(post("/api/timetables")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/timetables")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conflictPayload))
                .andExpect(status().isConflict());
    }

    @Test
    void createTimetable_SameRoomAndTimeDifferentDate_Returns201() throws Exception {
        createEnabledUser("admin-timetable-date@test.com", "pass123", UserRole.ROLE_ADMIN);
        String adminToken = loginAndGetAccessToken("admin-timetable-date@test.com", "pass123");

        Teacher teacher = createTeacher("teacher-date@test.com");
        Course course = createCourse(teacher, "Algorithms", "CS301");
        Room room = createRoom("Room-Date-101");

        String firstPayload = "{" +
                "\"date\":\"2026-05-23\"," +
                "\"day\":\"MONDAY\"," +
                "\"startTime\":\"09:00:00\"," +
                "\"endTime\":\"11:00:00\"," +
                "\"courseId\":" + course.getIdCourse() + "," +
                "\"roomId\":" + room.getId() +
                "}";

        String secondPayloadDifferentDate = "{" +
                "\"date\":\"2026-05-24\"," +
                "\"day\":\"TUESDAY\"," +
                "\"startTime\":\"09:00:00\"," +
                "\"endTime\":\"11:00:00\"," +
                "\"courseId\":" + course.getIdCourse() + "," +
                "\"roomId\":" + room.getId() +
                "}";

        mockMvc.perform(post("/api/timetables")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/timetables")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondPayloadDifferentDate))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value("2026-05-24"))
                .andExpect(jsonPath("$.roomId").value(room.getId()));
    }

    private User createEnabledUser(String email, String rawPassword, UserRole role) {
        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabeld(true);
        user.setRole(role);
        return userRepository.save(user);
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"" + email + "\"," +
                                "\"password\":\"" + password + "\"" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = new ObjectMapper().readTree(response);
        return jsonNode.get("accessToken").asText();
    }

    private Teacher createTeacher(String email) {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Teach");
        teacher.setLastName("Er");
        teacher.setEmail(email);
        teacher.setPassword(passwordEncoder.encode("pass123"));
        teacher.setEnabeld(true);
        teacher.setRole(UserRole.ROLE_TEACHER);
        teacher.setEmployeeNumber("EMP-" + Math.abs(email.hashCode()));
        teacher.setSpeciality("Computer Science");
        return teacherRepository.save(teacher);
    }

    private Course createCourse(Teacher teacher, String name, String code) {
        Course course = new Course();
        course.setName(name);
        course.setCode(code);
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    private Room createRoom(String name) {
        Room room = new Room();
        room.setName(name);
        room.setCapacity(40);
        room.setType("LECTURE");
        return roomRepository.save(room);
    }

}
