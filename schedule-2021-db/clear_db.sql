START TRANSACTION;

CREATE SCHEMA IF NOT EXISTS schema_schedule;
SET search_path = "schema_schedule";


DELETE FROM deadline WHERE TRUE;

DELETE FROM task WHERE TRUE;

DELETE FROM progress WHERE TRUE;

DELETE FROM profile_subject WHERE TRUE;

DELETE FROM profile WHERE TRUE;

DELETE FROM lesson_group WHERE TRUE;

DELETE FROM lesson_teacher WHERE TRUE;

DELETE FROM lesson WHERE TRUE;

DELETE FROM subject WHERE TRUE;

DELETE FROM semester WHERE TRUE;

DELETE FROM student WHERE TRUE;

DELETE FROM "GROUP" WHERE TRUE;

DELETE FROM department_specialization WHERE TRUE;

DELETE FROM specialization WHERE TRUE;

DELETE FROM teacher WHERE TRUE;

DELETE FROM lesson_type WHERE TRUE;

DELETE FROM subject_type WHERE TRUE;

DELETE FROM task_type WHERE TRUE;

DELETE FROM department WHERE TRUE;

DELETE FROM faculty WHERE TRUE;

COMMIT;
