package io.gradehub.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ptar
 * @since 1.0
 */

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orgId;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,  mappedBy = "organization")
    private List<Course> courses = new ArrayList<>();

    public Organization(String name) {
        this.name = name;
    }
}
