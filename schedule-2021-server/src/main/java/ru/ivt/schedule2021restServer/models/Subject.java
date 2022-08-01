package ru.ivt.schedule2021restServer.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "subject")
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonBackReference
    @Column(name = "lectures_amount")
    private Long lecturesAmount;

    @JsonBackReference
    @Column(name = "lab_amount")
    private Long labAmount;

    @JsonBackReference
    @Column(name = "practice_amount")
    private Long practiceAmount;

    @JsonBackReference
    @Column(name = "homework_amount")
    private Long homeworkAmount;

    @JsonBackReference
    @Column(name = "subject_type_id", nullable = false)
    private Long subjectTypeId;

    @OneToMany(mappedBy = "subject")
    private List<Lesson> lesson;

    @OneToMany(mappedBy = "subject")
    private List<Progress> progresses;
}
