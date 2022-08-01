package ru.ivt.schedule2021restServer.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "progress_id", nullable = false)
    private Long progressId;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Column(name = "result", nullable = false)
    private String result;

}
