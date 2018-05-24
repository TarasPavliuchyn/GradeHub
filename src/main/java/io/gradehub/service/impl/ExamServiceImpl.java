package io.gradehub.service.impl;

import io.gradehub.dto.ExamDto;
import io.gradehub.exception.ExamNotFoundException;
import io.gradehub.model.Exam;
import io.gradehub.repository.ExamRepository;
import io.gradehub.service.ExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author ptar
 * @since 1.0
 */
@Slf4j
@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Override
    public Page<ExamDto> findPaginatedExamByOrgAndId(Long orgId, Long courseId, int page, int size) {

        Page<Exam> course = examRepository.findAllByCourseAndOrg(PageRequest.of(page, size), orgId, courseId);

        return course.map(this::convertToDto);
    }

    @Override
    public ExamDto getExam(Long examId, Long courseId, Long orgId) {
        Exam exam = checkAndGetExam(examId, courseId, orgId);
        return convertToDto(exam);
    }

    private Exam checkAndGetExam(Long examId, Long courseId, Long orgId) {
        Exam exam = examRepository.findByIdAndCourseAndOrg(examId, courseId, orgId);
        if (exam == null) {
            log.error("Can't find exam by examId={} and courseId={} and orgId={}", examId, courseId, orgId);
            throw new ExamNotFoundException();
        }
        return exam;
    }

    @Override
    public void deleteExam(Long examId, Long courseId, Long orgId) {
        Exam exam = checkAndGetExam(examId, courseId, orgId);
        examRepository.delete(exam);
    }

    private ExamDto convertToDto(Exam exam) {
        ExamDto examDto = new ExamDto();
        examDto.setExamId(exam.getExamId());
        examDto.setName(exam.getName());
        return examDto;
    }
}
