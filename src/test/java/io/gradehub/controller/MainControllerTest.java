package io.gradehub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gradehub.dto.CourseDto;
import io.gradehub.model.Course;
import io.gradehub.model.Exam;
import io.gradehub.repository.CourseRepository;
import io.gradehub.repository.ExamRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author ptar
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class MainControllerTest {

    public static final long EXISTING_ORGANIZATION_ID = 1L;
    public static final long EXISTING_COURSE_ID = 2L;
    public static final long EXISTING_EXAM_ID = 3L;
    public static final long UNKNOWN_ORGANIZATION_ID = 11L;
    public static final long UNKNOWN_COURSE_ID = 11L;

    @LocalServerPort
    private int port;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private MockMvc restController;

    private String baseUrl;

    @Before
    public void setUp() {
        baseUrl = createBaseUrl(EXISTING_ORGANIZATION_ID);
    }

    private String createBaseUrl(Long organizationId) {
        return String.format("http://localhost:%s/organizations/%d/courses", port, organizationId);
    }

    @Test
    public void test_getAllCourses() throws Exception {
        restController.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].courseId", is(2)))
                .andExpect(jsonPath("$[0].name", is("Dissertation")))
                .andExpect(jsonPath("$[0].examsNumber", is(1)));

    }

    @Test
    public void test_getAllCourses_when_UnknownOrganization() throws Exception {
        restController.perform(get(createBaseUrl(UNKNOWN_ORGANIZATION_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_getCourseById() throws Exception {
        restController.perform(get(baseUrl + "/" + EXISTING_COURSE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.courseId", is(2)))
                .andExpect(jsonPath("$.name", is("Dissertation")))
                .andExpect(jsonPath("$.examsNumber", is(1)));
    }

    @Test
    public void test_getCourseById_when_NotFound() throws Exception {
        restController.perform(get(baseUrl + "/" + UNKNOWN_COURSE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void test_addCourse() throws Exception {

        MvcResult result = restController.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new CourseDto("Java"))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("Java")))
                .andExpect(jsonPath("$.examsNumber", is(0)))
                .andReturn();

        String location = (String) result
                .getResponse().getHeaderValue("location");
        assertNotNull(location);

        Integer idForCleanUp = Character.getNumericValue(location.charAt(location.length() - 1));
        courseRepository.deleteById(idForCleanUp.longValue());
    }

    @Test
    @WithMockUser(username = "user")
    public void test_addCourse_when_No_Permission() throws Exception {
        restController.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new CourseDto("Java"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void test_updateCourse() throws Exception {
        String newCourseName = "Java";
        restController.perform(put(baseUrl + "/" + EXISTING_COURSE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(new CourseDto(newCourseName))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is(newCourseName)))
                .andExpect(jsonPath("$.examsNumber", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void test_deleteCourse() throws Exception {
        Course courseForDeletion = courseRepository.save(new Course(null, "TO_DELETE"));
        Long courseId = courseForDeletion.getCourseId();

        restController.perform(delete(baseUrl + "/" + courseId))
                .andExpect(status().isNoContent());

        assertFalse(courseRepository.findById(courseId).isPresent());
    }

    @Test
    public void test_findExamsByCourse() throws Exception {
        String url = baseUrl + "/" + EXISTING_COURSE_ID + "/exams";
        restController.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.content[0].examId", is(3)))
                .andExpect(jsonPath("$.content[0].name", is("Law")));
    }

    @Test
    public void test_findExamsByCourse_when_Page_Not_Exists() throws Exception {
        String url = baseUrl + "/" + EXISTING_COURSE_ID + "/exams?page=100";

        restController.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_findExamsById() throws Exception {
        String url = baseUrl + "/" + EXISTING_COURSE_ID + "/exams/" + EXISTING_EXAM_ID;

        restController.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.examId", is(3)))
                .andExpect(jsonPath("$.name", is("Law")));
    }

    @Test
    public void test_findExamsByCourse_when_Exam_Not_Exists() throws Exception {
        String url = baseUrl + "/" + EXISTING_COURSE_ID + "/exams/" + 11;

        restController.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void test_deleteExamById() throws Exception {
        Course course = courseRepository.findById(EXISTING_COURSE_ID).get();
        Exam savedExam = examRepository.save(new Exam("Spring", course));
        Long examId = savedExam.getExamId();
        String url = baseUrl + "/" + EXISTING_COURSE_ID + "/exams/" + examId;

        restController.perform(delete(url))
                .andExpect(status().isNoContent());

        assertFalse(examRepository.findById(examId).isPresent());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}