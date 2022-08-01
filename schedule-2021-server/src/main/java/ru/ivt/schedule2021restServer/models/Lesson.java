package ru.ivt.schedule2021restServer.models;

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
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "lesson")
public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_date", nullable = false)
    private LocalDate lessonDate;

    @Column(name = "place")
    private String place;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type", nullable = false)
    private LessonType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "time_start", nullable = false)
    private String timeStart;

    @Column(name = "time_end", nullable = false)
    private String timeEnd;

    @ManyToMany
    @JoinTable(name = "lesson_group",
        joinColumns = {
            @JoinColumn(name = "lesson", referencedColumnName = "id", nullable = false)
        },
        inverseJoinColumns = {
            @JoinColumn(name = "\"GROUP\"", referencedColumnName = "id", nullable = false)
        })
    private List<GROUP> groupList;

    @ManyToMany
    @JoinTable(name = "lesson_teacher",
        joinColumns = {
            @JoinColumn(name = "lesson", referencedColumnName = "id", nullable = false)
        },
        inverseJoinColumns = {
            @JoinColumn(name = "teacher", referencedColumnName = "id", nullable = false)
        })
    private List<Teacher> teacherList;

}
