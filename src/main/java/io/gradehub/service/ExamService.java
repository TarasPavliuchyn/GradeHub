package io.gradehub.service;

import io.gradehub.dto.ExamDto;
import org.springframework.data.domain.Page;

/**
 * @author ptar
 * @since 1.0
 */
public interface ExamService {
    Page<ExamDto> findPaginatedExamByOrgAndId(Long orgId, Long courseId, int page, int size);

    ExamDto getExam(Long examId, Long courseId, Long orgId);

    void deleteExam(Long examId, Long courseId, Long orgId);
}
