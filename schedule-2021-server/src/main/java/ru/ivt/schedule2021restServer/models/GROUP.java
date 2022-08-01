package ru.ivt.schedule2021restServer.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "\"GROUP\"")
public class GROUP implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "api_id", nullable = false)
    @JsonBackReference
    private Long apiId;

    @Column(name = "level", nullable = false)
    private byte level;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "origin", nullable = false)
    private Byte origin;

    @Column(name = "year", nullable = false)
    @JsonBackReference
    private Long year;

    @Column(name = "department_specialization_id")
    @JsonBackReference
    private Long departmentSpecializationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @OneToMany(mappedBy = "group")
    private Set<Student> student;

    @ManyToMany
    @JoinTable(name = "lesson_group",
        joinColumns = {
            @JoinColumn(name = "\"GROUP\"", referencedColumnName = "id", nullable = false)
        },
        inverseJoinColumns = {
            @JoinColumn(name = "lesson", referencedColumnName = "id", nullable = false)
        })
    private List<Lesson> lesson;
}
