package io.gradehub.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author ptar
 * @since 1.0
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long examId;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    public Exam(String name, Course course) {
        this.name = name;
        this.course = course;
    }
}
