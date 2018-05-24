package io.gradehub.service.impl;

import io.gradehub.dto.CourseDto;
import io.gradehub.exception.CourseNotFoundException;
import io.gradehub.exception.OrganizationNotFoundException;
import io.gradehub.model.Course;
import io.gradehub.model.Organization;
import io.gradehub.repository.CourseRepository;
import io.gradehub.repository.OrganizationRepository;
import io.gradehub.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author ptar
 * @since 1.0
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses(Long orgId) {
        Organization organization = checkAndGetOrganization(orgId);
        Iterable<Course> allCourses = courseRepository.findAllByOrganization(organization);
        return StreamSupport.stream(allCourses.spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDto getCourseByOrgAndId(Long orgId, Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        return course.map(this::convertToDto).orElseThrow(CourseNotFoundException::new);
    }

    @Override
    @Transactional
    public CourseDto addCourse(Long orgId, CourseDto courseDto) {
        Organization organization = checkAndGetOrganization(orgId);
        Course newCourse = new Course(organization, courseDto.getName());
        Course course = courseRepository.save(newCourse);
        return convertToDto(course);
    }

    @Override
    @Transactional
    public CourseDto updateCourse(Long courseId, Long orgId, CourseDto updatedCourse) {
        checkAndGetOrganization(orgId);
        Optional<Course> course = courseRepository.findById(courseId);
        return course
                .map((cor) -> updateCourse(updatedCourse, cor))
                .map(this::convertToDto)
                .orElseThrow(CourseNotFoundException::new);
    }

    @Override
    @Transactional
    public CourseDto partialUpdateCourse(Long orgId, CourseDto updatedCourse) {
        checkAndGetOrganization(orgId);
        Optional<Course> course = courseRepository.findById(updatedCourse.getCourseId());
        return course
                .map((cor) -> partialUpdateCourse(updatedCourse, cor))
                .map(this::convertToDto)
                .orElseThrow(CourseNotFoundException::new);
    }

    @Override
    @Transactional
    public void deleteCourse(Long orgId, Long courseId) {
        checkAndGetOrganization(orgId);
        Optional<Course> course = courseRepository.findById(courseId);
        Course courseById = course.orElseThrow(CourseNotFoundException::new);
        courseRepository.delete(courseById);
    }

    private Organization checkAndGetOrganization(Long orgId) {
        return organizationRepository.findById(orgId).orElseThrow(OrganizationNotFoundException::new);
    }

    private CourseDto convertToDto(Course course) {
        CourseDto postDto = new CourseDto();
        postDto.setCourseId(course.getCourseId());
        postDto.setName(course.getName());
        postDto.setExamsNumber(course.getExams().size());
        return postDto;
    }

    private Course partialUpdateCourse(CourseDto updatedCourse, Course course) {
        String newName = updatedCourse.getName();
        if (newName != null) {
            course.setName(newName);
        }
        return course;
    }

    private Course updateCourse(CourseDto updatedCourse, Course course) {
        course.setName(updatedCourse.getName());
        return course;
    }
}
