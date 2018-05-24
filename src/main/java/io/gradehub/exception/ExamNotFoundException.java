package io.gradehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception throws if exam is not exists
 * @author ptar
 * @since 1.0
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ExamNotFoundException extends RuntimeException {
}