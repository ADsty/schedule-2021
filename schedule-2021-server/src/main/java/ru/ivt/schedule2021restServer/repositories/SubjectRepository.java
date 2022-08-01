package ru.ivt.schedule2021restServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ivt.schedule2021restServer.models.Subject;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long>, JpaSpecificationExecutor<Subject> {

    @Query("SELECT sub " +
        "FROM Subject sub " +
        "INNER JOIN sub.progresses as prog " +
        "WHERE prog.semester.level = :semesterLevel " +
        "AND prog.semester.student.id = :studentId")
    List<Subject> findSubjectsOfStudent(@Param("studentId") Long studentId,
                                        @Param("semesterLevel") Byte semesterLevel);

    Subject findSubjectByName(String name);
}
