package io.gradehub;

import io.gradehub.model.Course;
import io.gradehub.model.Exam;
import io.gradehub.model.Organization;
import io.gradehub.repository.OrganizationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author ptar
 * @since 1.0
 */

@SpringBootApplication
public class ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationRunner.class, args);
    }

    /**
     * Initial data
     */
    @Bean
    public CommandLineRunner demo(OrganizationRepository organizationRepository) {
        return (args) -> {
            Organization university = new Organization("University of Durham");
            Course dissertationCourse = new Course(university, "Dissertation");
            Exam law = new Exam("Law", dissertationCourse);
            law.setCourse(dissertationCourse);
            dissertationCourse.getExams().add(law);
            university.getCourses().add(dissertationCourse);

            organizationRepository.save(university);
        };
    }

}