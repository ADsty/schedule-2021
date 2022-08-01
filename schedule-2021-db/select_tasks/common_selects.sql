CREATE SCHEMA IF NOT EXISTS schema_schedule;
SET search_path = "schema_schedule";


/*
    Общее задание 1.

    Сделайте выборку всех данных из каждой таблицы
*/
SELECT *
FROM faculty;

SELECT *
FROM department;

SELECT *
FROM task_type;

SELECT *
FROM subject_type;

SELECT *
FROM lesson_type;

SELECT *
FROM teacher;

SELECT *
FROM specialization;

SELECT *
FROM department_specialization;

SELECT *
FROM "GROUP";

SELECT *
FROM student;

SELECT *
FROM semester;

SELECT *
FROM subject;

SELECT *
FROM lesson;

SELECT *
FROM lesson_teacher;

SELECT *
FROM lesson_group;

SELECT *
FROM profile;

SELECT *
FROM profile_subject;

SELECT *
FROM progress;

SELECT *
FROM task;

SELECT *
FROM deadline;

/*
    Общее задание 2.

    Сделайте выборку данных из одной таблицы при нескольких условиях,
    с использованием логических операций, LIKE, BETWEEN, IN
*/
SELECT profile.id, profile.name
FROM profile
WHERE profile.name LIKE '09.03.01%';

SELECT subject.lab_amount, subject.name
FROM subject
WHERE subject.lab_amount BETWEEN 27 AND 29;

SELECT l_subject_name__good_task_results__task_type.name,
       task_type.name,
       l_subject_name__good_task_results__task_type.result
FROM (SELECT subject.name, l_subjec_id__task_type_result.result, l_subjec_id__task_type_result.type_id
      FROM (SELECT progress.subject_id, task.type_id, task.result
            FROM progress
                     INNER JOIN task
                                ON progress.id = task.progress_id
           ) AS l_subjec_id__task_type_result
               INNER JOIN subject
                          ON l_subjec_id__task_type_result.subject_id = subject.id
      WHERE l_subjec_id__task_type_result.result IN ('Отлично', 'Хорошо')
     ) AS l_subject_name__good_task_results__task_type
         INNER JOIN task_type
                    ON l_subject_name__good_task_results__task_type.type_id = task_type.id;


/*
    Общее задание 3.

    Создайте в запросе вычисляемое поле.
*/
SELECT name, (lectures_amount + lab_amount + practice_amount + homework_amount) AS work_amount
FROM subject;


/*
    Общее задание 4.

    Сделайте выборку всех данных с сортировкой по нескольким полям.
*/
SELECT *
FROM "GROUP"
ORDER BY "GROUP".level, "GROUP".origin;

/*
    Общее задание 5.

    Создайте запрос, вычисляющий несколько совокупных характеристик таблиц.
*/
SELECT COUNT(*)                                                                               AS number_of_subjects,
       (AVG(lectures_amount) + AVG(lab_amount) + AVG(practice_amount) + AVG(homework_amount)) AS avg_work_amount,
       MIN(homework_amount)                                                                   AS minimum_homework,
       MAX(lectures_amount)                                                                   AS maximum_lectures
FROM subject;


/*
    Общее задание 6.

    Сделайте выборку данных из связанных таблиц (не менее двух примеров).
*/
SELECT student.name, "GROUP".name
FROM student
         LEFT OUTER JOIN "GROUP"
                         ON student.group_id = "GROUP".id;


SELECT subject.name,
       subject.lectures_amount,
       subject.lab_amount,
       subject.practice_amount,
       subject.homework_amount,
       subject_type.name
FROM subject
         INNER JOIN subject_type
                    ON subject.subject_type_id = subject_type.id;

/*
    Общее задание 7.

    Создайте запрос, рассчитывающий совокупную характеристику с использованием группировки,
    наложите ограничение на результат группировки.
*/
SELECT faculty.name, COUNT(*) AS number_of_departments
FROM department
         INNER JOIN faculty ON faculty.id = department.faculty_id
GROUP BY faculty.name
HAVING COUNT(*) < 8
ORDER BY faculty.name;

/*
    Общее задание 8.

    Придумайте и реализуйте пример использования вложенного запроса.
*/
SELECT "GROUP".name, l_lesson__group_id.lesson_date, l_lesson__group_id.time_start, l_lesson__group_id.time_end
FROM (SELECT lesson.*, lesson_group."GROUP" AS group_id
      FROM lesson
               LEFT JOIN lesson_group
                         ON lesson.id = lesson_group.lesson
     ) AS l_lesson__group_id
         INNER JOIN "GROUP"
                    ON l_lesson__group_id.group_id = "GROUP".id
ORDER BY "GROUP".name, l_lesson__group_id.lesson_date, l_lesson__group_id.time_start;

/*
    Общее задание 9.

    С помощью оператора INSERT добавьте в каждую таблицу по одной записи
*/
DO
$$
    DECLARE
        faculty_id      faculty.id%TYPE;
        department_id   department.id%TYPE;
        task_type_id    task_type.id%TYPE;
        subject_type_id subject_type.id%TYPE;
        lesson_type_id  lesson_type.id%TYPE;
        teacher_id      teacher.id%TYPE;
        spec_id         specialization.id%TYPE;
        dep_spec_id     department_specialization.id%TYPE;
        group_id        "GROUP".id%TYPE;
        student_id      student.id%TYPE;
        semester_id     semester.id%TYPE;
        subject_id      subject.id%TYPE;
        lesson_id       lesson.id%TYPE;
        profile_id      profile.id%TYPE;
        progress_id     progress.id%TYPE;
    BEGIN

        INSERT INTO faculty(name, short)
        VALUES ('Институт программистов', 'ИП')
        RETURNING id INTO faculty_id;

        INSERT INTO department(name, faculty_id)
        VALUES ('Кафедра программистов', faculty_id)
        RETURNING id INTO department_id;

        INSERT INTO task_type(id, name)
        VALUES ((CASE
                     WHEN (SELECT MAX(id) FROM task_type) IS NOT NULL THEN (SELECT MAX(id) FROM task_type) + 1
                     ELSE 1 END),
                'Задание')
        RETURNING id INTO task_type_id;

        INSERT INTO subject_type(id, name)
        VALUES (DEFAULT, 'Внеучебная Практика')
        RETURNING id INTO subject_type_id;

        INSERT INTO lesson_type(id, name)
        VALUES ((CASE
                     WHEN (SELECT MAX(id) FROM lesson_type) IS NOT NULL THEN (SELECT MAX(id) FROM lesson_type) + 1
                     ELSE 1 END),
                'Занятие')
        RETURNING id INTO lesson_type_id;

        INSERT INTO teacher(name, api_id)
        VALUES ('Петров Виталий Дмитриевич', '96024')
        RETURNING id INTO teacher_id;

        INSERT INTO specialization(name)
        VALUES ('01.01.01 Образование')
        RETURNING id INTO spec_id;

        INSERT INTO department_specialization(department_id, specialization_id)
        VALUES (department_id, spec_id)
        RETURNING id INTO dep_spec_id;

        INSERT INTO "GROUP"(name, api_id, level, type, origin, year, department_specialization_id)
        VALUES ('3530901/80201_Fake', '96024', 3, 'common', 0, 2020, dep_spec_id)
        RETURNING id INTO group_id;

        INSERT INTO student(name, group_id)
        VALUES ('Петров Виталий Дмитриевич', group_id)
        RETURNING id INTO student_id;

        INSERT INTO semester(student_id, level)
        VALUES (student_id, 6)
        RETURNING id INTO semester_id;

        INSERT INTO subject(id, name, lectures_amount, lab_amount, practice_amount, homework_amount, subject_type_id)
        VALUES ((CASE WHEN (SELECT MAX(id) FROM subject) IS NOT NULL THEN (SELECT MAX(id) FROM subject) + 1 ELSE 1 END),
                'Предмет', 100, 100, 100, 100, subject_type_id)
        RETURNING id INTO subject_id;

        INSERT INTO lesson(lesson_date, place, type, subject_id, time_start, time_end)
        VALUES ('2021-05-26', 'Дистанционно', lesson_type_id, subject_id, '10:00', '20:00')
        RETURNING id INTO lesson_id;

        INSERT INTO lesson_teacher(lesson, teacher)
        VALUES (lesson_id, teacher_id);

        INSERT INTO lesson_group(lesson, "GROUP")
        VALUES (lesson_id, group_id);

        INSERT INTO profile(specialization_id, name)
        VALUES (spec_id, '01.01.01_01 Программисты')
        RETURNING id INTO profile_id;

        INSERT INTO profile_subject(profile, subject)
        VALUES (profile_id, subject_id);

        INSERT INTO deadline(title, description, notification_time, deadline_date, type, subject_id, lesson_id,
                             media_url)
        VALUES ('Работа', 'Сделать работу', '10:00:00', '2021-05-26', 'Deadline', subject_id, lesson_id, 'vk.com');

        INSERT INTO progress(semester_id, subject_id)
        VALUES (semester_id, subject_id)
        RETURNING id INTO progress_id;

        INSERT INTO task(progress_id, type_id, result)
        VALUES (progress_id, task_type_id, 'Отчислен');
    END
$$;

/*
    Общее задание 10.

    С помощью оператора UPDATE измените значения нескольких полей у всех записей,
    отвечающих заданному условию.
*/
UPDATE deadline
SET deadline.description = CONCAT('!!Сделать срочно!! ', deadline.description),
    deadline.media_URL   = 'https://dl.spbstu.ru/mod/assign/view.php?id=123456'
WHERE deadline.id = 2;

/*
    Общее задание 11.

    С помощью оператора DELETE удалите запись,
    имеющую максимальное (минимальное) значение некоторой совокупной характеристики.
*/
DELETE
FROM deadline
WHERE deadline.deadline_date = (SELECT MAX(deadline.deadline_date)
                                FROM deadline);

/*
    Общее задание 12.

    С помощью оператора DELETE удалите записи в главной таблице,
    на которые не ссылается подчиненная таблица (используя вложенный запрос).
*/
DELETE
FROM faculty
WHERE faculty.id NOT IN (SELECT department.faculty_id
                         FROM department)