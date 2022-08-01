START TRANSACTION;

CREATE SCHEMA IF NOT EXISTS schema_schedule;
SET search_path = "schema_schedule";


SELECT * FROM faculty;

SELECT * FROM department;

SELECT * FROM task_type;

SELECT * FROM subject_type;

SELECT * FROM lesson_type;

SELECT * FROM teacher;

SELECT * FROM specialization;

SELECT * FROM department_specialization;

SELECT * FROM "GROUP";

SELECT * FROM student;

SELECT * FROM semester;

SELECT * FROM subject;

SELECT * FROM lesson;

SELECT * FROM lesson_teacher;

SELECT * FROM lesson_group;

SELECT * FROM profile;

SELECT * FROM profile_subject;

SELECT * FROM progress;

SELECT * FROM task;

SELECT * FROM deadline;

COMMIT;