package io.gradehub.repository;

import io.gradehub.model.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author ptar
 * @since 1.0
 */
public interface ExamRepository extends PagingAndSortingRepository<Exam, Long> {

    @Query("SELECT ex FROM Exam ex INNER JOIN ex.course c INNER JOIN c.organization o WHERE c.courseId = :courseId AND o.orgId = :orgId")
    Page<Exam> findAllByCourseAndOrg(Pageable pageable, @Param("orgId") Long orgId, @Param("courseId") Long courseId);

    @Query("SELECT ex FROM Exam ex INNER JOIN ex.course c INNER JOIN c.organization o WHERE c.courseId = :courseId AND o.orgId = :orgId AND ex.examId = :examId")
    Exam findByIdAndCourseAndOrg(@Param("examId") Long examId, @Param("courseId") Long courseId, @Param("orgId") Long orgId);
}