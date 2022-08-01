import org.intellij.lang.annotations.Language

@Language("PostgreSQL")
const val preparedSubjectSQL = """
    INSERT INTO schema_schedule.subject (name, lectures_amount, lab_amount, practice_amount, homework_amount, subject_type_id) 
    SELECT ?, ?, ?, ?, ?, ?
    WHERE NOT EXISTS (
        SELECT 1
        FROM schema_schedule.subject
        WHERE name = ?
    )
"""

@Language("PostgreSQL")
const val preparedLessonSQL = """
    INSERT INTO schema_schedule.lesson (lesson_date, place, type, subject_id, time_start, time_end) 
    SELECT ?, ?, ?, ?, ?, ?
    WHERE NOT EXISTS (
        SELECT 1
        FROM schema_schedule.lesson
        WHERE lesson_date = ?
        AND place = ?
        AND type = ?
        AND subject_id = ?
        AND time_start = ?
        AND time_end = ?
    )
"""

@Language("PostgreSQL")
const val preparedLessonTeacherSQL = """
    INSERT INTO schema_schedule.lesson_teacher (lesson, teacher) 
    SELECT ?, ?
    WHERE NOT EXISTS(
        SELECT 1
        FROM schema_schedule.lesson_teacher
        WHERE lesson = ?
        AND teacher = ?
    )
"""

@Language("PostgreSQL")
const val preparedLessonGroupSQL =  """
    INSERT INTO schema_schedule.lesson_group (lesson, "GROUP") 
    SELECT ?, ?
    WHERE NOT EXISTS(
        SELECT 1
        FROM schema_schedule.lesson_group
        WHERE lesson = ?
        AND "GROUP" = ?
    )
"""

@Language("PostgreSQL")
const val preparedSemestersSQL = """
    INSERT INTO schema_schedule.semester (student_id, level) 
    SELECT ?, ?
    WHERE NOT EXISTS (
        SELECT 1 
        FROM schema_schedule.semester 
        WHERE student_id = ?
        AND level = ?
    )
"""

@Language("PostgreSQL")
const val preparedTaskTypeSQL = """
    INSERT INTO schema_schedule."task_type" (id, name) 
    SELECT ?, ?
    WHERE NOT EXISTS (
        SELECT 1 
        FROM schema_schedule.task_type 
        WHERE name = ?          
    )
"""

@Language("PostgreSQL")
const val preparedProfileSubjectSQL = """
    INSERT INTO schema_schedule."profile_subject" (profile, subject) 
    SELECT ?, ?
    WHERE NOT EXISTS (
        SELECT 1 
        FROM schema_schedule.profile_subject
        WHERE profile = ?
        AND subject = ?
    )
"""

@Language("PostgreSQL")
const val preparedLessonTypeSQL = """
    INSERT INTO schema_schedule."lesson_type" (id, name) 
    SELECT ?, ?
    WHERE NOT EXISTS (
        SELECT 1 
        FROM schema_schedule.lesson_type 
        WHERE id = ?
        AND name = ?      
    )
"""
