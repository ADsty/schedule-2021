package ru.ivt.schedule2021restServer.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "lesson_teacher")
public class LessonTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "lesson", nullable = false)
    private Long lesson;

    @Column(name = "teacher", nullable = false)
    private Long teacher;

}
