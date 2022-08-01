package ru.ivt.schedule2021restServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ivt.schedule2021restServer.models.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    @Query("SELECT o_lesson " +
        "FROM GROUP AS o_group " +
        "INNER JOIN o_group.lesson AS o_lesson " +
        "ON o_group.id = :groupId " +
        "AND o_lesson.lessonDate " +
        "BETWEEN :weekStart AND :weekEnd")
    List<Lesson> findCurrentWeekLessonsOfGroup(@Param("groupId") Long groupId,
                                               @Param("weekStart") LocalDate weekStart,
                                               @Param("weekEnd") LocalDate weekEnd);

    Lesson findLessonById(Long id);
}
