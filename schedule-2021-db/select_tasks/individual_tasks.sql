--  noinspection NonAsciiCharactersForFile

CREATE SCHEMA IF NOT EXISTS schema_schedule;
SET search_path = "schema_schedule";


SET SESSION schedule_vars.first_semester_start_date = '2020-09-01';
SET SESSION schedule_vars.first_semester_end_date = '2021-01-31';
SET SESSION schedule_vars.second_semester_start_date = '2021-02-01';
SET SESSION schedule_vars.second_semester_end_date = '2021-08-31';

/*
    Индивидуальное задание 1

    Вывести 5 наиболее сложных институтов.
    Сложность института считать как среднее значение
    произведения количества дисциплин в семестре
    на количество заданий по каждой дисциплине.
*/
SELECT l_faculty_name__multiplied_tasks_and_semesters.faculty_name            AS институт,
       ROUND(AVG(l_faculty_name__multiplied_tasks_and_semesters.muptiply), 1) AS сложность
FROM (SELECT fac_name_sem_id_progr_cnt.faculty_name,
             SUM(l_tasks_amount__semester_id.tasks_amount) * /* Количество дисциплин в семестре */
             COUNT(fac_name_sem_id_progr_cnt.progress_amount_per_semester) AS muptiply
      FROM (SELECT l_tasks_amount__progress_id.tasks_amount, progress.semester_id
            FROM (SELECT COUNT(*) AS tasks_amount, progress_id
                  FROM task
                  GROUP BY progress_id) AS l_tasks_amount__progress_id
                     INNER JOIN progress ON l_tasks_amount__progress_id.progress_id = progress.id) AS l_tasks_amount__semester_id
               INNER JOIN
           (SELECT l_faculty_name_semester_id.faculty_name,
                   progress.semester_id,
                   COUNT(*) AS progress_amount_per_semester /* Количество дисциплин */
            FROM (SELECT DISTINCT ON (semester.student_id) l_faculty_name_student_id.faculty_name, semester.id
                  FROM (SELECT DISTINCT ON (l_faculty_name_group_id.id) l_faculty_name_group_id.faculty_name, student.id
                        FROM (SELECT l_faculty_name__dep_spec_id.faculty_name, "GROUP".id
                              FROM (SELECT l_faculty_name__department_id.faculty_name,
                                           dep_spec.id
                                    FROM (SELECT faculty.name AS faculty_name, department.id
                                          FROM faculty
                                                   INNER JOIN department ON faculty.id = department.faculty_id
                                         ) AS l_faculty_name__department_id
                                             INNER JOIN department_specialization AS dep_spec
                                                        ON l_faculty_name__department_id.id =
                                                           dep_spec.department_id
                                   ) AS l_faculty_name__dep_spec_id
                                       INNER JOIN "GROUP"
                                                  ON l_faculty_name__dep_spec_id.id =
                                                     "GROUP".department_specialization_id
                             ) AS l_faculty_name_group_id
                                 INNER JOIN student ON student.group_id = l_faculty_name_group_id.id
                       ) AS l_faculty_name_student_id
                           INNER JOIN semester ON semester.student_id = l_faculty_name_student_id.id
                 ) AS l_faculty_name_semester_id
                     INNER JOIN progress ON progress.semester_id = l_faculty_name_semester_id.id
            GROUP BY progress.semester_id, l_faculty_name_semester_id.faculty_name
           ) AS fac_name_sem_id_progr_cnt
           ON fac_name_sem_id_progr_cnt.semester_id = l_tasks_amount__semester_id.semester_id
      GROUP BY l_tasks_amount__semester_id.semester_id, fac_name_sem_id_progr_cnt.faculty_name
     ) AS l_faculty_name__multiplied_tasks_and_semesters
GROUP BY l_faculty_name__multiplied_tasks_and_semesters.faculty_name
ORDER BY сложность DESC
LIMIT 5;
/*
    /Индивидуальное задание 1
*/


/*
    Индивидуальное задание 2

    Вывести преподавателей,
    которые каждый год с момента начала работы увеличивают количество дисциплин,
    которые преподают в рамках одного учебного года.
*/

CREATE OR REPLACE FUNCTION COUNT_SUBJECTS_OF_TEACHER_PER_SEMESTER(semester_month_start DATE, semester_month_end DATE)
    RETURNS TABLE
            (
                teacher_id    INTEGER,
                teacher_name  TEXT,
                subject_count INTEGER
            )
AS
'SELECT l_teacher_id_name__subject_id.teacher_id,
        l_teacher_id_name__subject_id.name,
        COUNT(*)
 FROM (SELECT l_teacher_id_name__lesson_id.teacher_id,
              l_teacher_id_name__lesson_id.name,
              lesson.subject_id
       FROM lesson
                INNER JOIN
            (SELECT teacher.id            AS teacher_id,
                    teacher.name,
                    lesson_teacher.lesson AS lesson_id
             FROM teacher
                      INNER JOIN lesson_teacher ON teacher.id = lesson_teacher.teacher
            ) AS l_teacher_id_name__lesson_id
            ON l_teacher_id_name__lesson_id.lesson_id = lesson.id
       WHERE (lesson.lesson_date BETWEEN semester_month_start AND semester_month_end)
       GROUP BY l_teacher_id_name__lesson_id.teacher_id, l_teacher_id_name__lesson_id.name, lesson.subject_id
      ) AS l_teacher_id_name__subject_id
 GROUP BY l_teacher_id_name__subject_id.teacher_id, l_teacher_id_name__subject_id.name'
    LANGUAGE sql
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;

SELECT l_semester_1.teacher_name  AS преподаватель,
       l_semester_1.subject_count AS количество_в_первом_семестре,
       l_semester_2.subject_count AS количество_во_втором_семестре
FROM (SELECT *
      FROM COUNT_SUBJECTS_OF_TEACHER_PER_SEMESTER(CURRENT_SETTING('schedule_vars.first_semester_start_date')::DATE,
                                                  CURRENT_SETTING('schedule_vars.first_semester_end_date')::DATE)) AS l_semester_1
         INNER JOIN
     (SELECT *
      FROM COUNT_SUBJECTS_OF_TEACHER_PER_SEMESTER(CURRENT_SETTING('schedule_vars.second_semester_start_date')::DATE,
                                                  CURRENT_SETTING('schedule_vars.second_semester_end_date')::DATE)) AS l_semester_2
     ON l_semester_1.teacher_id = l_semester_2.teacher_id AND l_semester_1.subject_count < l_semester_2.subject_count;


--     Выводит даты занятий и предмет для конкретного преподавателя
SELECT lesson_date, subject_id
FROM lesson AS L
         INNER JOIN (SELECT lesson FROM lesson_teacher WHERE teacher = 2625 /* ID Преподавателя указать здесь */) AS L_T
                    ON L.id = L_T.lesson
WHERE lesson_date BETWEEN '2021-02-01' AND '2021-04-15'
GROUP BY subject_id, lesson_date;
/*
    /Индивидуальное задание 2
*/

/*
    Индивидуальное задание 3

    Вывести агрегированный учебный план заданного студента:
    по каждому семестру обучения вывести дисциплины,
    виды занятий по каждой, задачи по каждой.
*/
SELECT l_student_name__level__subject_name_id__task_type.student_name  AS студент,
       l_student_name__level__subject_name_id__task_type.level         AS семестр,
       l_student_name__level__subject_name_id__task_type.subject_name  AS дисциплины,
       l_student_name__level__subject_name_id__task_type.task_type_seq AS виды_занятий,
       l_student_name__subject_type__lesson_type.lesson_type_seq       AS задачи
FROM (SELECT l_student_name__lesson_type_id__subject_id.student_name,
             l_student_name__lesson_type_id__subject_id.subject_id,
             STRING_AGG(lesson_type.name, ',') AS lesson_type_seq
      FROM (SELECT l_student_name__lesson_id.student_name, lesson.type, lesson.subject_id
            FROM (SELECT l_student_name__group_id.student_name, lesson_group.lesson
                  FROM (SELECT student.name AS student_name, "GROUP".id
                        FROM student
                                 INNER JOIN "GROUP" ON "GROUP".id = student.group_id
                       ) AS l_student_name__group_id
                           INNER JOIN lesson_group ON lesson_group."GROUP" = l_student_name__group_id.id
                 ) AS l_student_name__lesson_id
                     INNER JOIN lesson ON lesson.id = l_student_name__lesson_id.lesson
            GROUP BY l_student_name__lesson_id.student_name, lesson.subject_id, lesson.type
           ) AS l_student_name__lesson_type_id__subject_id
               INNER JOIN lesson_type
                          ON l_student_name__lesson_type_id__subject_id.type = lesson_type.id
      GROUP BY l_student_name__lesson_type_id__subject_id.student_name,
               l_student_name__lesson_type_id__subject_id.subject_id
     ) AS l_student_name__subject_type__lesson_type
         INNER JOIN
     (SELECT l_student_name__level__subject_name_id__task_type.student_name,
             l_student_name__level__subject_name_id__task_type.level,
             l_student_name__level__subject_name_id__task_type.subject_name,
             l_student_name__level__subject_name_id__task_type.subject_id,
             STRING_AGG(task_type.name, ',') AS task_type_seq
      FROM (SELECT l_student_name__level__subject_id__progress_id.student_name,
                   l_student_name__level__subject_id__progress_id.level,
                   subject.name AS subject_name,
                   subject.id   AS subject_id,
                   task.type_id
            FROM (SELECT l_student_name__semester_id_level.student_name,
                         l_student_name__semester_id_level.level,
                         progress.subject_id,
                         progress.id AS progress_id
                  FROM (SELECT student.name AS student_name, semester.id, semester.level
                        FROM student
                                 INNER JOIN semester ON student.id = semester.student_id
                       ) AS l_student_name__semester_id_level
                           INNER JOIN progress ON progress.semester_id = l_student_name__semester_id_level.id
                 ) AS l_student_name__level__subject_id__progress_id
                     INNER JOIN subject ON subject.id = l_student_name__level__subject_id__progress_id.subject_id
                     INNER JOIN task ON task.progress_id = l_student_name__level__subject_id__progress_id.progress_id
           ) AS l_student_name__level__subject_name_id__task_type
               INNER JOIN task_type
                          ON task_type.id = l_student_name__level__subject_name_id__task_type.type_id
      GROUP BY l_student_name__level__subject_name_id__task_type.student_name,
               l_student_name__level__subject_name_id__task_type.level,
               l_student_name__level__subject_name_id__task_type.subject_name,
               l_student_name__level__subject_name_id__task_type.subject_id
     ) AS l_student_name__level__subject_name_id__task_type
     ON l_student_name__level__subject_name_id__task_type.student_name =
        l_student_name__subject_type__lesson_type.student_name
         AND l_student_name__subject_type__lesson_type.subject_id =
             l_student_name__level__subject_name_id__task_type.subject_id;
/*
    /Индивидуальное задание 3
*/


/*
    Индивидуальное задание 4

    Вывести расписание дедлайнов заданного студента по неделям заданного семестра.
*/

CREATE OR REPLACE FUNCTION WEEK_SCHEDULE_OF_DEADLINES(semester_start DATE, semester_end DATE, studentID INTEGER)
    RETURNS TABLE
            (
                student_id        INTEGER,
                student_name      TEXT,
                week_start        DATE,
                deadline_date     DATE,
                notification_time TIME,
                title             TEXT,
                description       TEXT,
                type              TEXT,
                subject_id        INTEGER
            )
AS
'SELECT l_existing_deadlines.studentID,
        l_existing_deadlines.name,
        l_all_semester_weeks.all_weeks,
        l_existing_deadlines.deadline_date,
        l_existing_deadlines.notification_time,
        l_existing_deadlines.title,
        l_existing_deadlines.description,
        l_existing_deadlines.type,
        l_existing_deadlines.subject_id
 FROM (SELECT *
       FROM GENERATE_SERIES(semester_start, semester_end, ''1 week'') AS all_weeks) AS l_all_semester_weeks
          LEFT JOIN
      (SELECT studentID,
              student.name,
              week_start,
              week_order_deadlines.deadline_date,
              week_order_deadlines.notification_time,
              week_order_deadlines.title,
              week_order_deadlines.description,
              week_order_deadlines.type,
              week_order_deadlines.subject_id
       FROM (SELECT DATE_TRUNC(''week'', deadline.deadline_date::DATE)::DATE AS week_start,
                    deadline.deadline_date,
                    deadline.notification_time,
                    deadline.title,
                    deadline.description,
                    deadline.type,
                    deadline.subject_id,
                    deadline.lesson_id,
                    deadline.student_id
             FROM deadline
             WHERE deadline.student_id = studentID
               AND deadline_date BETWEEN semester_start AND semester_end
             ORDER BY week_start, deadline_date, notification_time) AS week_order_deadlines
                INNER JOIN student ON student.id = week_order_deadlines.student_id) AS l_existing_deadlines
      ON l_existing_deadlines.week_start = l_all_semester_weeks.all_weeks'
    LANGUAGE sql
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;

SELECT *
FROM WEEK_SCHEDULE_OF_DEADLINES(CURRENT_SETTING('schedule_vars.second_semester_start_date')::DATE,
                                CURRENT_SETTING('schedule_vars.second_semester_end_date')::DATE, 1500);
/*
    /Индивидуальное задание 4
*/

/*
    Индивидуальное задание 5
    
    Вывести наиболее простой и наиболее трудный (по количеству зачетов) семестр для заданной группы.
*/
SELECT l_group_id_name__rank_max_min__level__sum_of_tasks.name AS группа,
       (CASE
            WHEN rank_max = 1 AND rank_min != 1 THEN 'наиболее трудный'
            WHEN rank_max != 1 AND rank_min = 1 THEN 'наиболее простой'
            WHEN rank_max = 1 AND rank_min = 1 THEN 'один семестр'
           END)                                                AS сложность_семестра,
       level                                                   AS семестр,
       sum_of_tasks_per_semester                               AS количество_зачётов
FROM (SELECT l_group_id_name__level__sum_of_tasks.group_id,
             l_group_id_name__level__sum_of_tasks.group_name,
             DENSE_RANK() OVER (PARTITION BY l_group_id_name__level__sum_of_tasks.group_name
                 ORDER BY l_group_id_name__level__sum_of_tasks.sum_of_tasks_per_semester DESC) AS rank_max,
             DENSE_RANK() OVER (PARTITION BY l_group_id_name__level__sum_of_tasks.group_name
                 ORDER BY l_group_id_name__level__sum_of_tasks.sum_of_tasks_per_semester)      AS rank_min,
             l_group_id_name__level__sum_of_tasks.level,
             l_group_id_name__level__sum_of_tasks.sum_of_tasks_per_semester
      FROM (SELECT l_group_id_name__level__progress_id.group_id,
                   l_group_id_name__level__progress_id.group_name,
                   l_group_id_name__level__progress_id.level,
                   COUNT(*) AS sum_of_tasks_per_semester
            FROM (SELECT l_group_id_name__semester_id_level.group_id,
                         l_group_id_name__semester_id_level.group_name,
                         l_group_id_name__semester_id_level.level,
                         progress.id AS progress_id
                  FROM (SELECT l_group_id_name__student_id.group_id,
                               l_group_id_name__student_id.group_name,
                               semester.id AS semester_id,
                               semester.level
                        FROM (SELECT DISTINCT ON ("GROUP".id) "GROUP".id   AS group_id,
                                                              "GROUP".name AS group_name,
                                                              student.id   AS student_id
                              FROM "GROUP"
                                       INNER JOIN student ON "GROUP".id = student.group_id
                             ) AS l_group_id_name__student_id
                                 INNER JOIN semester ON semester.student_id = l_group_id_name__student_id.student_id
                       ) AS l_group_id_name__semester_id_level
                           INNER JOIN progress ON progress.semester_id = l_group_id_name__semester_id_level.semester_id
                 ) AS l_group_id_name__level__progress_id
                     INNER JOIN task
                                ON task.progress_id = l_group_id_name__level__progress_id.progress_id AND
                                   task.type_id = 1 /* ID ЗАЧËТА  */
            GROUP BY l_group_id_name__level__progress_id.group_id, l_group_id_name__level__progress_id.group_name,
                     l_group_id_name__level__progress_id.level
           ) AS l_group_id_name__level__sum_of_tasks
     ) AS l_group_id_name__rank_max_min__level__sum_of_tasks
WHERE /*l_group_id_name__rank_max_min__level__sum_of_tasks.name
        in ('3430302/90001','3530904/70103','3531201/80201','3630102/80401','3631503/00001','3834101/00001','в3135401/80301','в3743805/90501')
         /* Группы, для  которых созданы два семестра*/
  AND*/ (l_group_id_name__rank_max_min__level__sum_of_tasks.rank_max = 1 OR
         l_group_id_name__rank_max_min__level__sum_of_tasks.rank_min = 1)
ORDER BY group_id, сложность_семестра;

-- 3430302/90001 - группа, для которой есть три семестра
/*
    /Индивидуальное задание 5
*/

/*
    Индивидуальное задание 6
    
    Вывести преподавателей,
    у которых занятия есть только в осеннем или весеннем семестре,
    но при этом суммарная нагрузка у них выше,
    чем средняя нагрузка преподавателей,
    которые реализуют те же профили.
*/

CREATE OR REPLACE FUNCTION GET_TEACHING_DATES(study_year INTEGER)
    RETURNS TABLE
            (
                teacher_id        INTEGER,
                teacher_name      TEXT,
                lesson_date       DATE,
                lesson_subject_id INTEGER
            )
AS
'SELECT l_teacher_id_name__lesson_id.teacher_id,
        l_teacher_id_name__lesson_id.name,
        lesson_date,
        subject_id
 FROM lesson
          INNER JOIN
      (SELECT teacher.id            AS teacher_id,
              teacher.name,
              lesson_teacher.lesson AS lesson_id
       FROM teacher
                INNER JOIN lesson_teacher ON teacher.id = lesson_teacher.teacher) AS l_teacher_id_name__lesson_id
      ON l_teacher_id_name__lesson_id.lesson_id = lesson.id
 WHERE lesson_date BETWEEN MAKE_DATE(study_year, 09, 01) AND MAKE_DATE(study_year + 1, 08, 31)'
    LANGUAGE sql
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION GET_BUSIER_TEACHER_THAN_AVERAGE(study_year INTEGER)
    RETURNS TABLE
            (
                преподаватель        TEXT,
                нагрузка             FLOAT,
                средняя_нагрузка     FLOAT,
                профиль_преподавания TEXT
            )
AS
'SELECT teacher_name,
        l_single_semester_teachers.lesson_amount,
        ROUND(l_teaching_profile__profile_average_lesson_amount.lesson_amount, 1),
        ARRAY_TO_STRING(
                ARRAY(SELECT name
                      FROM subject
                      WHERE id = ANY (l_single_semester_teachers.teaching_profile::int[])),
                '', '')::TEXT AS teacher_profile
 FROM (SELECT teacher_id,
              teacher_name, /*first_lesson_date, last_lesson_date,*/
              lesson_amount,
              teaching_profile
       FROM (SELECT teacher_id,
                    teacher_name,
                    MIN(lesson_date)                            AS first_lesson_date,
                    MAX(lesson_date)                            AS last_lesson_date,
                    COUNT(*)                                    AS lesson_amount,
                    ARRAY_AGG(DISTINCT lesson_subject_id::TEXT) AS teaching_profile
             FROM GET_TEACHING_DATES(study_year)
             GROUP BY teacher_id, teacher_name) AS l_teacher_id_name__first_last_lesson_date
       WHERE EXTRACT(MONTH FROM first_lesson_date) NOT IN (9, 10, 11, 12, 1)
          OR EXTRACT(MONTH FROM last_lesson_date) NOT IN (2, 3, 4, 5, 6, 7, 8)) AS l_single_semester_teachers
          INNER JOIN
      (SELECT teaching_profile,
           AVG(lesson_amount) AS lesson_amount
       FROM (SELECT COUNT(*)                                    AS lesson_amount,
                    ARRAY_AGG(DISTINCT lesson_subject_id::TEXT) AS teaching_profile
             FROM GET_TEACHING_DATES(study_year)
             GROUP BY teacher_id, teacher_name) AS l_teaching_profile__lesson_amount
       GROUP BY teaching_profile) AS l_teaching_profile__profile_average_lesson_amount
      ON l_single_semester_teachers.teaching_profile =
         l_teaching_profile__profile_average_lesson_amount.teaching_profile
          AND
         l_single_semester_teachers.lesson_amount > l_teaching_profile__profile_average_lesson_amount.lesson_amount'
    LANGUAGE sql
    IMMUTABLE
    RETURNS NULL ON NULL INPUT;

SELECT *
FROM GET_BUSIER_TEACHER_THAN_AVERAGE(
        EXTRACT(YEAR FROM current_setting('schedule_vars.first_semester_start_date')::DATE)::INTEGER)
/*
    /Индивидуальное задание 6
*/
