package ru.ivt.schedule2021restServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ivt.schedule2021restServer.models.Deadline;

import java.time.LocalDate;
import java.util.List;

public interface DeadlineRepository extends JpaRepository<Deadline, Long>, JpaSpecificationExecutor<Deadline> {

    @Query("SELECT o_deadline " +
        "FROM Deadline o_deadline " +
        "INNER JOIN o_deadline.student o_student " +
        "WHERE o_student.id = :studentId " +
        "AND o_deadline.deadlineDate " +
        "BETWEEN :weekStart AND :weekEnd")
    List<Deadline> findDeadlinesOfCurrentWeekByStudent(@Param("studentId") Long studentId,
                                                       @Param("weekStart") LocalDate weekStart,
                                                       @Param("weekEnd") LocalDate weekEnd);

    @Query("SELECT o_deadline " +
        "FROM Deadline o_deadline " +
        "INNER JOIN o_deadline.group o_group " +
        "WHERE o_group.id = :groupId " +
        "AND o_deadline.deadlineDate " +
        "BETWEEN :weekStart AND :weekEnd")
    List<Deadline> findDeadlinesOfCurrentWeekByGroup(@Param("groupId") Long groupId,
                                                     @Param("weekStart") LocalDate weekStart,
                                                     @Param("weekEnd") LocalDate weekEnd);

    List<Deadline> findDeadlinesByStudentId(Long studentId);
}
