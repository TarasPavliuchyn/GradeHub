package io.gradehub.repository;

import io.gradehub.model.Course;
import io.gradehub.model.Organization;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author ptar
 * @since 1.0
 */
public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findAllByOrganization(Organization organization);
}
