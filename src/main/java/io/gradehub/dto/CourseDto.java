package io.gradehub.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author ptar
 * @since 1.0
 */
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CourseDto {
    private Long courseId;
    private String name;
    private int examsNumber;

    public CourseDto(String name) {
        this.name = name;
    }
}
