package io.gradehub.controller;

import io.gradehub.dto.CourseDto;
import io.gradehub.dto.ExamDto;
import io.gradehub.exception.ExamNotFoundException;
import io.gradehub.exception.OrganizationNotFoundException;
import io.gradehub.service.CourseService;
import io.gradehub.service.ExamService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * It's main controller that invokes CRUD operations
 *
 * @author ptar
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/organizations/{orgId}/courses")
public class MainController {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "5";

    @Autowired
    private CourseService courseService;
    @Autowired
    private ExamService examService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<CourseDto> getCourses(@PathVariable("orgId") Long orgId) {
        log.info("Trying to get courses of university [{}]", orgId);
        return courseService.getAllCourses(orgId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{courseId}", method = RequestMethod.GET)
    public CourseDto getCourseById(@PathVariable("orgId") Long orgId, @PathVariable Long courseId) {
        log.info("Trying to get course by orgId={} and courseId={}", orgId, courseId);
        return courseService.getCourseByOrgAndId(orgId, courseId);
    }

    /**
     * {@link OrganizationNotFoundException} may be thrown if {orgId} is unknown
     * Only user with role 'ADMIN' can invoke this end-point.
     *
     * @param orgId  unique identifier of organization
     * @param course - body of the HTTP request
     */
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseDto> addCourse(@PathVariable("orgId") Long orgId, @RequestBody CourseDto course) {
        log.info("Trying to add course={} for organization={}", course, orgId);

        CourseDto courseDto = courseService.addCourse(orgId, course);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(courseDto.getCourseId()).toUri();

        return ResponseEntity
                .created(location)
                .body(courseDto);
    }

    /**
     * This method perform partial update.
     * Only user with role 'ADMIN' can invoke this end-point.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{courseId}", method = RequestMethod.PATCH)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CourseDto partialUpdateCourse(@PathVariable("orgId") Long orgId, @PathVariable Long courseId, @RequestBody CourseDto course) {
        log.info("Trying to partialUpdate course={} for organization={}", courseId, orgId);
        return courseService.partialUpdateCourse(orgId, course);
    }

    /**
     * Only user with role 'ADMIN' can invoke this end-point.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{courseId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CourseDto updateCourse(@PathVariable("orgId") Long orgId, @PathVariable Long courseId, @RequestBody CourseDto course) {
        log.info("Trying to update course={} for organization={}", courseId, orgId);
        return courseService.updateCourse(courseId, orgId, course);
    }

    /**
     * Only user with role 'ADMIN' can invoke this end-point.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{courseId}", method = RequestMethod.DELETE)
    public void deleteCourse(@PathVariable("orgId") Long orgId, @PathVariable Long courseId) {
        log.info("Trying to delete course={} for organization={}", courseId, orgId);
        courseService.deleteCourse(orgId, courseId);
    }

    /**
     * This endpoint retrieves exams by {@code orgId} and {@code courseId}
     *
     * @param orgId    - unique identifier of organization
     * @param courseId - unique identifier of course
     * @param page     - zero-based page index.
     * @param size     - the size of the page to be returned.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{courseId}/exams", method = RequestMethod.GET)
    public SimplePage<ExamDto> getExamsByCourse(@PathVariable("orgId") Long orgId, @PathVariable Long courseId,
                                                @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(value = "size", required = false, defaultValue = DEFAULT_SIZE) int size) {
        log.info("Trying to get exams by courseId={}", courseId);

        Page<ExamDto> examResult = examService.findPaginatedExamByOrgAndId(orgId, courseId, page, size);
        if (page > examResult.getTotalPages()) {
            throw new ExamNotFoundException();
        }

        return toCustomPage(examResult);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{courseId}/exams/{examId}", method = RequestMethod.GET)
    public ExamDto getExamById(@PathVariable Long orgId, @PathVariable Long courseId, @PathVariable Long examId) {
        log.info("Trying to get exam by examId={} and courseId={} and orgId={}", examId, courseId, orgId);
        return examService.getExam(examId, courseId, orgId);
    }

    /**
     * Only user with role 'ADMIN' can invoke this end-point.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{courseId}/exams/{examId}", method = RequestMethod.DELETE)
    public void deleteExam(@PathVariable Long orgId, @PathVariable Long courseId, @PathVariable Long examId) {
        log.info("Trying to delete exam by examId={} and courseId={} and orgId={}", examId, courseId, orgId);
        examService.deleteExam(examId, courseId, orgId);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    // have to create custom type because
    // https://stackoverflow.com/questions/34099559/how-to-consume-pageentity-response-using-spring-resttemplate/34103753?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    public static class SimplePage<T> {
        private List<T> content;
        private int totalPages;
    }

    private <T> SimplePage<T> toCustomPage(Page<T> page) {
        SimplePage<T> simplePage = new SimplePage<>();
        simplePage.setContent(page.getContent());
        simplePage.setTotalPages(page.getTotalPages());
        return simplePage;
    }

}