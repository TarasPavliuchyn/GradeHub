package io.gradehub.service;

import io.gradehub.dto.CourseDto;

import java.util.List;

/**
 * @author ptar
 * @since 1.0
 */
public interface CourseService {
    List<CourseDto> getAllCourses(Long orgId);

    CourseDto getCourseByOrgAndId(Long orgId, Long courseId);

    CourseDto addCourse(Long orgId, CourseDto course);

    CourseDto updateCourse(Long courseId, Long orgId, CourseDto course);

    CourseDto partialUpdateCourse(Long orgId, CourseDto course);

    void deleteCourse(Long orgId, Long courseId);
}
