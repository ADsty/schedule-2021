START TRANSACTION;

CREATE SCHEMA IF NOT EXISTS schema_schedule;
SET search_path = "schema_schedule";

DROP TABLE IF EXISTS FACULTY CASCADE;
CREATE TABLE FACULTY (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL
);

DROP TABLE IF EXISTS DEPARTMENT CASCADE;
CREATE TABLE DEPARTMENT (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL,
    FACULTY_ID INTEGER REFERENCES FACULTY (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS SPECIALIZATION CASCADE;
CREATE TABLE SPECIALIZATION (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL,
    DEPARTMENT_ID INTEGER REFERENCES DEPARTMENT (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS "GROUP" CASCADE;
CREATE TABLE "GROUP" (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL,
    SPECIALIZATION_ID INTEGER REFERENCES SPECIALIZATION (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS STUDENT CASCADE;
CREATE TABLE STUDENT (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL,
    GROUP_ID INTEGER REFERENCES "GROUP" (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS SUBJECT CASCADE;
CREATE TABLE SUBJECT (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL,
    SPECIALIZATION_ID INTEGER REFERENCES SPECIALIZATION (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS LESSON_TYPE CASCADE;
CREATE TABLE LESSON_TYPE (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL
);

DROP TABLE IF EXISTS LESSON CASCADE;
CREATE TABLE LESSON (
    ID SERIAL PRIMARY KEY,
    LESSON_DATE DATE NOT NULL,
    PLACE TEXT,
    TYPE INTEGER REFERENCES LESSON_TYPE (ID) ON DELETE CASCADE NOT NULL,
    SUBJECT_ID INTEGER REFERENCES SUBJECT (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS LESSON_GROUP;
CREATE TABLE LESSON_GROUP (
    ID SERIAL PRIMARY KEY,
    LESSON INTEGER REFERENCES LESSON (ID) ON DELETE CASCADE NOT NULL,
    "GROUP" INTEGER REFERENCES "GROUP" (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS TEACHER CASCADE;
CREATE TABLE TEACHER (
    ID SERIAL PRIMARY KEY,
    NAME TEXT NOT NULL
);

DROP TABLE IF EXISTS LESSON_TEACHER;
CREATE TABLE LESSON_TEACHER (
    ID SERIAL PRIMARY KEY,
    LESSON INTEGER REFERENCES LESSON (ID) ON DELETE CASCADE NOT NULL,
    TEACHER INTEGER REFERENCES TEACHER (ID) ON DELETE CASCADE NOT NULL
);

DROP TABLE IF EXISTS DEADLINE;
CREATE TABLE DEADLINE (
    ID SERIAL PRIMARY KEY,
    TITLE TEXT NOT NULL,
    DESCRIPTION TEXT,
    NOTIFICATION_TIME TIME NOT NULL,
    DEADLINE_DATE DATE NOT NULL,
    TYPE TEXT NOT NULL,
    SUBJECT_ID INTEGER REFERENCES SUBJECT(ID) ON DELETE CASCADE NOT NULL,
    LESSON_ID INTEGER REFERENCES LESSON (ID) ON DELETE CASCADE,
    MEDIA_URL TEXT
);

COMMIT;
