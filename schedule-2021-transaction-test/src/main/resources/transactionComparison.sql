SET search_path = schema_schedule;

-- Консоль 1

/* Сравнение READ COMMITTED с REPEATABLE READ */
BEGIN TRANSACTION ISOLATION LEVEL READ COMMITTED ;
UPDATE deadline set title = '8' where id = 1;
COMMIT;

BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
UPDATE deadline set title = '10' where id = 1;
COMMIT;
/* Сравнение READ COMMITTED с REPEATABLE READ */

/* Сравнение REPEATABLE READ с SERIALIZABLE */
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
insert into lesson_type values (40, '');
select sum(id) from lesson_type;

commit ;

BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE ;
insert into lesson_type values (20, '');
select sum(id) from lesson_type;

COMMIT;
/* Сравнение REPEATABLE READ с SERIALIZABLE */

ROLLBACK;


-- Консоль 2

/* Сравнение READ COMMITTED с REPEATABLE READ */
BEGIN TRANSACTION ISOLATION LEVEL READ COMMITTED ;
UPDATE deadline set title = '7' where id = 1;
COMMIT;

BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
UPDATE deadline set title = '9' where id = 1;
COMMIT;
/* Сравнение READ COMMITTED с REPEATABLE READ */

/* Сравнение REPEATABLE READ с SERIALIZABLE */
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ ;
insert into lesson_type values (30, '');
select sum(id) from lesson_type;

commit;

BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE ;
insert into lesson_type values (25, '');
select sum(id) from lesson_type;

COMMIT;
/* Сравнение REPEATABLE READ с SERIALIZABLE */

ROLLBACK;
