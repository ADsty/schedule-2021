import java.lang.Exception
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.time.LocalDate
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun insertAllTables(connection: Connection) {

    var duration = measureTime { insertFaculty(connection) }
    logger.log("[FACULTY] DURATION = $duration\n====")

    duration = measureTime { insertDepartment(connection) }
    logger.log("[DEPARTMENT] DURATION = $duration\n====")

    duration = measureTime { insertTaskType(connection) }
    logger.log("[TASK_TYPE] DURATION = $duration\n====")

    duration = measureTime { insertSubjectType(connection) }
    logger.log("[SUBJECT_TYPE] DURATION = $duration\n====")

    duration = measureTime { insertLessonType(connection) }
    logger.log("[LESSON_TYPE] DURATION = $duration\n====")

    duration = measureTime { insertTeacher(connection) }
    logger.log("[TEACHER] DURATION = $duration\n====")

    duration = measureTime { insertGroupSpecializationAndGroupSpecialization(connection) }
    logger.log("[GROUP SPECIALIZATION GROUP_SPECIALIZATION] DURATION = $duration\n====")

    duration = measureTime { insertStudent(connection) }
    logger.log("[STUDENTS] DURATION = $duration\n====")

    duration = measureTime { insertSemester(connection) }
    logger.log("[SEMESTERS] DURATION = $duration\n====")

    duration = measureTime { insertSubjectLessonLessonTeacherLessonGroup(connection) }
    logger.log("[SUBJECT LESSON LESSON_TEACHER LESSON_GROUP] DURATION = $duration\n====")

    duration = measureTime { insertProfile(connection) }
    logger.log("[PROFILE] DURATION = $duration\n====")

    duration = measureTime { insertProfileSubject(connection) }
    logger.log("[PROFILE_SUBJECT] DURATION = $duration\n====")

    duration = measureTime { insertDeadline(connection) }
    logger.log("[DEADLINE] DURATION = $duration\n====")

    duration = measureTime { insertProgress(connection) }
    logger.log("[PROGRESS] DURATION = $duration\n====")

    duration = measureTime { insertTask(connection) }
    logger.log("[TASK] DURATION = $duration\n====")
}

fun insertFaculty(connection: Connection) {
    val tableName = "[FACULTY]"
    logger.log("$tableName INSERTION STARTED")

    val facultyBatch = connection.createStatement()
    val response = getApiResponse(ApiURL.FACULTIES)

    val facultiesContainer = gson.fromJson(response, FacultiesContainer::class.java)

    if (facultiesContainer.error == true) {
        return
    }

    for (faculty in facultiesContainer.facultyList) {
        with(faculty) {
            facultyBatch.addBatch(
                """
                    INSERT INTO schema_schedule.faculty (name, short)
                    SELECT '$name', '$short'
                    WHERE NOT EXISTS (
                        SELECT 1
                        FROM schema_schedule.faculty
                        WHERE name = '$name'
                        AND short = '$short'
                    )"""
            )
        }
    }

    executeBatch(facultyBatch, tableName)
}

fun insertDepartment(connection: Connection) {
    val tableName = "[DEPARTMENT]"
    logger.log("$tableName INSERTION STARTED")

    val departmentBatch = connection.createStatement()

    val facultyNameToIdMap = mutableMapOf<String, Int>()
    val facultyApiIdToNameMap = mutableMapOf<Int, String>()

    val facultyResultSet = connection.createStatement().executeQuery("SELECT id, name FROM schema_schedule.faculty")

    while (facultyResultSet.next()) {
        val name = facultyResultSet.getString("name")
        val facultyID = facultyResultSet.getInt("id")
        facultyNameToIdMap[name] = facultyID
    }

    val divisionURL = StringBuilder(ApiURL.DEPARTMENT)
    val divisionLength = divisionURL.length
    var apiPage = 1

    val divisionFullList = mutableListOf<Division>()

    do {
        if (apiPage % 20 == 0) {
            logger.log("$tableName Page =  $apiPage")
        }

        divisionURL.append("$apiPage")
        val response = getApiResponse(divisionURL.toString(), true)
        divisionURL.setLength(divisionLength)
        apiPage++

        val divisionContainer = gson.fromJson(response, DivisionContainer::class.java)

        if (isPlanResponseCorrect(divisionContainer, tableName)) {
            return
        }

        divisionFullList.addAll(divisionContainer.divisionList)

    } while (divisionContainer.next != null)

    for (division in divisionFullList) {
        if (division.closeDate == null && facultyNameToIdMap.containsKey(division.name)) {
            facultyApiIdToNameMap[division.apiId] = division.name
        }
    }

    for (department in divisionFullList) {
        val departmentParent = facultyApiIdToNameMap[department.parentId] ?: continue

        if (department.closeDate == null) {
            val facultyID = facultyNameToIdMap[departmentParent]
            departmentBatch.addBatch(
                """
                INSERT INTO schema_schedule.department (name, faculty_id)
                SELECT '${department.name}', $facultyID
                WHERE NOT EXISTS(
                SELECT 1
                FROM schema_schedule.department
                WHERE name = '${department.name}'
                AND faculty_id = $facultyID)""".trimMargin()
            )
        }
    }

    executeBatch(departmentBatch, tableName)
}

fun insertTaskType(connection: Connection) {
    val tableName = "[TASK_TYPE]"
    logger.log("$tableName INSERTION STARTED")

    val taskTypeStatement = connection.prepareStatement(preparedTaskTypeSQL)

    val taskNameList = listOf("Зачет", "Контрольная работа", "Коллоквиум", "Реферат", "КП", "КР", "Экзамен")

    with(taskTypeStatement) {
        for ((i, taskName) in taskNameList.withIndex()) {
            val taskId = i + 1
            setInt(1, taskId)
            setString(2, taskName)
            setString(3, taskName)
            addBatch()
        }
    }

    executeBatch(taskTypeStatement, tableName)
}

fun insertSubjectType(connection: Connection) {
    val tableName = "[SUBJECT_TYPE]"
    logger.log("$tableName INSERTION STARTED")

    val subjectTypeBatch = connection.createStatement()

    val response = getApiResponse(ApiURL.SUBTYPE, true)
    val subjectTypeContainer = gson.fromJson(response, SubjectTypeContainer::class.java)

    if (isPlanResponseCorrect(subjectTypeContainer, tableName)) {
        return
    }

    for (subjectType in subjectTypeContainer.subjectTypeList) {
        val subjectTypeName = subjectType.name

        subjectTypeBatch.addBatch(
            """
                INSERT INTO schema_schedule.subject_type(name)
                    SELECT '$subjectTypeName'
                    WHERE NOT EXISTS(
                    SELECT 1
                    FROM schema_schedule.subject_type
                    WHERE name = '$subjectTypeName'
                    )"""
        )
    }

    executeBatch(subjectTypeBatch, tableName)
}

fun insertLessonType(connection: Connection) {
    val tableName = "[LESSON_TYPE]"
    logger.log("$tableName INSERTION STARTED")

    val lessonTypeBatch = connection.prepareStatement(preparedLessonTypeSQL)

    val lessonTypeList = listOf(
        "Практика", "Лабораторные", "Лекции", "Зачет", "Консультации", "Дифференцированный зачет",
        "Курсовая работа", "Курсовой проект", "Экзамен", "Доп. экзамен", "Доп.сессия. Зачет",
        "Доп.сессия. Диф. Зачет", "Доп.сессия. Курсовой проект"
    )

    for ((lessonTypeId, lessonType) in lessonTypeList.withIndex()) {
        with(lessonTypeBatch) {
            setInt(1, lessonTypeId)
            setInt(3, lessonTypeId)
            setString(2, lessonType)
            setString(4, lessonType)
            addBatch()
        }
    }

    executeBatch(lessonTypeBatch, tableName)
}

fun insertTeacher(connection: Connection) {
    val tableName = "[TEACHER]"
    logger.log("$tableName INSERTION STARTED")

    val teacherBatch = connection.createStatement()
    val response = getApiResponse(ApiURL.TEACHERS)

    val teachersContainer = gson.fromJson(response, TeachersContainer::class.java)

    if (teachersContainer.error == true) {
        return
    }

    val teacherList = teachersContainer.teacherList
    for ((i, teacher) in teacherList.withIndex()) {
        logger.logId(i, teacherList.size / 10, tableName)

        with(teacher) {
            teacherBatch.addBatch(
                """
                INSERT INTO schema_schedule.teacher (name, api_id) 
                SELECT '$name', $apiId
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM schema_schedule.teacher
                    WHERE name = '$name'
                    AND api_id = '$apiId'
                )
                """
            )
        }
    }

    executeBatch(teacherBatch, tableName)
}

fun insertGroupSpecializationAndGroupSpecialization(connection: Connection) {
    val tableName = "[GROUP] [SPECIALIZATION] [DEPARTMENT_SPECIALIZATION]"
    logger.log("$tableName INSERTION STARTED")

    val specIdQuery = connection.createStatement()
    val specInsert = connection.createStatement()
    val depSpecInsert = connection.createStatement()
    val depSpecIdQuery = connection.createStatement()
    val groupBatch = connection.createStatement()

    val depIds = fastIdSelecting(connection, "department")

    val response = getApiResponse(ApiURL.GROUP)

    val groupContainer = gson.fromJson(response, GroupContainer::class.java)

    if (groupContainer.error == true) {
        return
    }

    for ((i, group) in groupContainer.groupList.withIndex()) {
        if ((i % (groupContainer.groupList.size / 10)) == 0) {
            logger.log("$tableName ID = $i")
        }

        with(group) {
            val specRows: Int
            try {
                specRows = specInsert.executeUpdate(
                    """
                INSERT INTO schema_schedule.specialization (name)
                SELECT '$spec'
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM schema_schedule.specialization
                    WHERE name = '$spec'
                )""", RETURN_GENERATED_KEYS
                )
            } catch (e: SQLException) {
                if (DEV_MODE) {
                    logger.log("$tableName SPEC INSERTION ERROR")
                    e.printStackTrace()
                }
                return@with
            }
            val specId = if (specRows == 0) {
                specIdQuery.executeQuery(
                    """
                    SELECT id
                    FROM schema_schedule.specialization
                    WHERE name = '$spec'
                """
                ).apply { next() }.getInt(1)
            } else {
                specInsert.generatedKeys.apply { next() }.getInt(1)
            }

            val depId = depIds.random()

            val depSpecRows: Int
            val isSpecHasMoreDepartments = specRows != 0 || (specRows == 0 && Random.nextFloat() < 0.05)
            if (isSpecHasMoreDepartments) {
                try {
                    depSpecRows = depSpecInsert.executeUpdate(
                        """
                INSERT INTO schema_schedule.department_specialization (department_id, specialization_id) 
                SELECT $depId, $specId
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM schema_schedule.department_specialization
                    WHERE department_id = $depId
                    AND specialization_id = $specId
                )""", RETURN_GENERATED_KEYS
                    )
                } catch (e: SQLException) {
                    if (DEV_MODE) {
                        logger.log("$tableName DEP SPEC INSERTION ERROR")
                        e.printStackTrace()
                    }
                    return@with
                }
            } else {
                depSpecRows = 0
            }

            val depSpecId = if (depSpecRows == 0) {
                val depSpecIdResultSet = depSpecIdQuery.executeQuery(
                    """
                    SELECT id
                    FROM schema_schedule.department_specialization
                    WHERE specialization_id = '$specId'
                """
                )
                if (depSpecIdResultSet.next()) {
                    depSpecIdResultSet.getInt(1)
                } else {
                    if (DEV_MODE) {
                        logger.log("$tableName DEP SPEC INSERTION Exception")
                    }
                    return@with
                }
            } else {
                val depSpecGeneratedKeys = depSpecInsert.generatedKeys
                if (depSpecGeneratedKeys.next()) {
                    depSpecGeneratedKeys.getInt(1)
                } else {
                    if (DEV_MODE) {
                        logger.log("$tableName DEP SPEC GENERATED KEYS Exception")
                    }
                    return@with
                }
            }

            groupBatch.addBatch(
                """
                    INSERT INTO schema_schedule."GROUP" (name, level, type, origin, year, department_specialization_id, api_id)
                    SELECT '$name', $level, '$type', $origin, $year, $depSpecId, $api_id
                    WHERE NOT EXISTS (
                        SELECT 1
                        FROM schema_schedule."GROUP"
                        WHERE name = '$name'
                        AND level = ${group.level}
                        AND type = '$type'
                        AND origin = ${group.origin}
                        AND year = ${group.year}
                        AND department_specialization_id = '$depSpecId'
                    )"""
            )
        }
    }

    executeBatch(groupBatch, tableName)
}

fun insertStudent(connection: Connection) {
    val tableName = "[STUDENT]"
    logger.log("$tableName INSERTION STARTED")

    val studentBatch = connection.createStatement()

    for (i in 0..GENERATED_STUDENT_AMOUNT) {
        if (i % (GENERATED_STUDENT_AMOUNT / 10) == 0) {
            logger.log("$tableName ID = $i")
        }

        val fakeName = faker.name
        val studentName = "${fakeName.firstName()} ${fakeName.lastName()}".replace("\'", "")

        val groupIds = fastIdSelecting(connection, "\"GROUP\"")
        val groupId = groupIds.random()

        studentBatch.addBatch(
            """
            INSERT INTO schema_schedule.student(name, group_id)
                SELECT '$studentName', $groupId
                WHERE NOT EXISTS(
                SELECT 1
                FROM schema_schedule.student
                WHERE name = '$studentName'            
                AND group_id = $groupId
                )"""
        )
    }

    executeBatch(studentBatch, tableName)
}

fun insertSemester(connection: Connection) {
    val tableName = "[SEMESTER]"
    logger.log("$tableName INSERTION STARTED")

    val studentIdLevelMap = mutableMapOf<Int, IntArray>()

    val studentIdAndLevelResultSet = connection.createStatement().executeQuery(
        """
        SELECT S.id, G.level
        FROM schema_schedule.student as S
        INNER JOIN schema_schedule."GROUP" G
        ON S.group_id = G.id
    """
    )

    while (studentIdAndLevelResultSet.next()) {
        val studentId = studentIdAndLevelResultSet.getInt(1)
        val groupLevel = studentIdAndLevelResultSet.getInt(2)

        studentIdLevelMap[studentId] = if (parsedFirstSemester) {
            intArrayOf(groupLevel * 2 - 1)
        } else if (parsedSecondSemester) {
            intArrayOf(groupLevel * 2)
        } else if (parsedBothSemesters) {
            intArrayOf(groupLevel * 2 - 1, groupLevel * 2)
        } else {
            if (DEV_MODE) {
                logger.log("$tableName Error while mapping semesters to scheduler date")
            }
            continue
        }
    }

    val semestersBatch = connection.prepareStatement(preparedSemestersSQL)

    var i = 0
    for ((studentId, groupLevel) in studentIdLevelMap.entries) {
        for (semesterLevel in groupLevel) {
            logger.logId(i++, studentIdLevelMap.size / 2, tableName)

            with(semestersBatch) {
                setInt(1, studentId)
                setInt(2, semesterLevel)
                setInt(3, studentId)
                setInt(4, semesterLevel)
                addBatch()
            }
        }
    }
    executeBatch(semestersBatch, tableName)
}

fun insertSubjectLessonLessonTeacherLessonGroup(connection: Connection) {
    val tableName = "[LESSON] [SUBJECT] [LESSON_TEACHER] [LESSON_GROUP]"
    logger.log("$tableName INSERTION STARTED")

    val subjectIdQuery = connection.createStatement()
    val lessonIdQuery = connection.createStatement()
    val groupIdQuery = connection.createStatement()

    val sbSubjectURL = StringBuilder("${ApiURL.TEACHERS}/")
    val sbSubjectURLSize = sbSubjectURL.length

    val subjectInsert = connection.prepareStatement(preparedSubjectSQL, RETURN_GENERATED_KEYS)
    val preparedLesson = connection.prepareStatement(preparedLessonSQL, RETURN_GENERATED_KEYS)
    val lessonTeacherBatch = connection.prepareStatement(preparedLessonTeacherSQL)
    val lessonGroupBatch = connection.prepareStatement(preparedLessonGroupSQL)

    val subjectTypeIdList = fastIdSelecting(connection, "subject_type")

    var schedulerDateCurrent = LocalDate.parse(SCHEDULER_DATE_START)
    val schedulerDateEnd = LocalDate.parse(SCHEDULER_DATE_END)


    while (schedulerDateCurrent.isBefore(schedulerDateEnd)) {
        val teacherIdAndApiIdSet =
            connection.createStatement().executeQuery("SELECT id, api_id FROM schema_schedule.teacher")

        while (teacherIdAndApiIdSet.next()) {
            val teacherId = teacherIdAndApiIdSet.getInt(1)
            val teacherApiId = teacherIdAndApiIdSet.getInt(2)

            sbSubjectURL.append(teacherApiId.toString() + ApiURL.SCHEDULER + schedulerDateCurrent)
            val response = getApiResponse(sbSubjectURL.toString())
            sbSubjectURL.setLength(sbSubjectURLSize)

            subjectInsert.apply {
                setInt(2, Random.nextInt(20, 41))
                setInt(3, Random.nextInt(20, 41))
                setInt(4, Random.nextInt(20, 41))
                setInt(5, Random.nextInt(20, 41))
                setInt(6, subjectTypeIdList.random())
            }

            val scheduler = gson.fromJson(response, Scheduler::class.java)

            if (scheduler.error == true) {
                if (DEV_MODE) {
                    logger.log("$tableName URL Error")
                }
                continue
            }

            for (day in scheduler.dayList) {
                preparedLesson.setDate(1, day.date)
                preparedLesson.setDate(7, day.date)

                for (lesson in day.lessonList) {
                    val subject = lesson.subject
                    subjectInsert.setString(1, subject)
                    subjectInsert.setString(7, subject)

                    val subjectInsertedRows: Int
                    try {
                        subjectInsertedRows = subjectInsert.executeUpdate()
                    } catch (e: SQLException) {
                        if (DEV_MODE) {
                            logger.log("$tableName SUBJECT INSERTION Exception")
                            e.printStackTrace()
                        }
                        continue
                    }

                    val subjectId = if (subjectInsertedRows == 0) {
                        val subjectIdResultSet = subjectIdQuery.executeQuery(
                            """
                            SELECT id 
                            FROM schema_schedule.subject 
                            WHERE name = '$subject'
                            """
                        )
                        if (subjectIdResultSet.next()) {
                            subjectIdResultSet.getInt(1)
                        } else {
                            if (DEV_MODE) {
                                logger.log("$tableName SUBJECT ID Not found")
                            }
                            continue
                        }
                    } else {
                        val subjectGeneratedKeys = subjectInsert.generatedKeys
                        if (subjectGeneratedKeys.next()) {
                            subjectGeneratedKeys.getInt(1)
                        } else {
                            if (DEV_MODE) {
                                logger.log("$tableName SUBJECT ID Not inserted")
                            }
                            continue
                        }
                    }

                    var lessonPlace = ""
                    for (place in lesson.placeList) {
                        lessonPlace += place.name + " "
                    }
                    with(preparedLesson) {

                        setString(2, lessonPlace)
                        setString(8, lessonPlace)
                        setInt(3, lesson.type)
                        setInt(9, lesson.type)
                        setInt(4, subjectId)
                        setInt(10, subjectId)
                        setString(5, lesson.timeStart)
                        setString(11, lesson.timeStart)
                        setString(6, lesson.timeEnd)
                        setString(12, lesson.timeEnd)
                    }
                    var lessonInsertedRows: Int
                    try {
                        lessonInsertedRows = preparedLesson.executeUpdate()
                    } catch (e: SQLException) {
                        if (DEV_MODE) {
                            logger.log("$tableName LESSON INSERTION Exception")
                            e.printStackTrace()
                        }
                        continue
                    }

                    val lessonId = if (lessonInsertedRows == 0) {
                        val lessonIdResultSet = lessonIdQuery.executeQuery(
                            """
                            SELECT id 
                            FROM schema_schedule.lesson
                            WHERE lesson_date = '${day.date}'
                            AND place = '$lessonPlace'
                            AND type = '${lesson.type}'
                            AND subject_id = '$subjectId'
                            AND time_start = '${lesson.timeStart}'
                            AND time_end = '${lesson.timeEnd}'
                        """
                        )
                        if (lessonIdResultSet.next()) {
                            lessonIdResultSet.getInt(1)
                        } else {
                            if (DEV_MODE) {
                                logger.log("$tableName LESSON ID Not found")
                            }
                            continue
                        }
                    } else {
                        val lessonGeneratedKeys = preparedLesson.generatedKeys
                        if (lessonGeneratedKeys.next()) {
                            lessonGeneratedKeys.getInt(1)
                        } else {
                            if (DEV_MODE) {
                                logger.log("$tableName LESSON ID Not inserted")
                            }
                            continue
                        }
                    }

                    lessonTeacherBatch.apply {
                        setInt(1, lessonId)
                        setInt(3, lessonId)
                        setInt(2, teacherId)
                        setInt(4, teacherId)
                        addBatch()
                    }

                    for (group in lesson.groupList) {
                        val groupId: Int
                        try {
                            groupId = groupIdQuery.executeQuery(
                                """
                            SELECT id 
                            FROM schema_schedule."GROUP" 
                            WHERE name = '${group.name}'
                            AND type = '${group.type}'
                            AND origin = ${group.origin}
                            AND year = ${group.year}
                            """
                            ).apply { next() }.getInt(1)
                        } catch (e: Exception) {
                            if (DEV_MODE) {
                                logger.log("$tableName GROUP ID SELECT Exception")
                                e.printStackTrace()
                            }
                            continue
                        }

                        lessonGroupBatch.apply {
                            setInt(1, lessonId)
                            setInt(3, lessonId)
                            setInt(2, groupId)
                            setInt(4, groupId)
                            addBatch()
                        }
                    }
                }
            }
        }

        executeBatch(lessonTeacherBatch, tableName, isInsertionFinish = false)
        executeBatch(lessonGroupBatch, tableName, isInsertionFinish = false)

        if (SCHEDULER_PARSE_ONLY_FIRST_WEEK) {
            break
        }

        logger.log("$tableName DATE = $schedulerDateCurrent")
        schedulerDateCurrent = schedulerDateCurrent.plusWeeks(1)
    }

    logger.log("$tableName INSERTION FINISHED")
}

fun insertProfile(connection: Connection) {
    val tableName = "[PROFILE]"
    logger.log("$tableName INSERTION STARTED")

    val specializationQuery = connection.createStatement()
    val profileBatch = connection.createStatement()

    val profilesURL = StringBuilder(ApiURL.PROFILE)
    val profilesLength = profilesURL.length

    var apiPage = 1
    do {
        if (apiPage % 5 == 0) {
            logger.log("$tableName PAGE = $apiPage")
        }

        profilesURL.append("$apiPage")
        val response = getApiResponse(profilesURL.toString(), true)
        profilesURL.setLength(profilesLength)

        val profilesContainer = gson.fromJson(response, ProfilesContainer::class.java)

        if (isPlanResponseCorrect(profilesContainer, tableName)) {
            return
        }

        for (profile in profilesContainer.profileList) {
            val profileFullName = profile.code + " " + profile.name
            val specIdResultSet = specializationQuery.executeQuery(
                """
                SELECT id
                FROM schema_schedule.specialization
                WHERE name LIKE '${profile.code.substring(0, profile.code.length - 4)}%'
            """
            )
            val specId = if (specIdResultSet.next()) {
                specIdResultSet.getInt(1)
            } else {
                if (DEV_MODE) {
                    logger.log("$tableName on page $apiPage missed profile $profileFullName")
                }
                continue
            }

            profileBatch.addBatch(
                """
                INSERT INTO schema_schedule.profile(name, specialization_id)
                    SELECT '$profileFullName', $specId
                    WHERE NOT EXISTS(
                    SELECT 1
                    FROM schema_schedule.profile
                    WHERE name = '$profileFullName'            
                    AND specialization_id = $specId
                    )"""
            )
        }
        apiPage++
    } while (profilesContainer.next != null)

    executeBatch(profileBatch, tableName)
}

fun insertProfileSubject(connection: Connection) {
    val tableName = "[PROFILE_SUBJECT]"
    logger.log("$tableName INSERTION STARTED")

    val profileIds = fastIdSelecting(connection, "profile")
    val subjectIds = fastIdSelecting(connection, "subject")

    val profileSubjectBatch = connection.prepareStatement(preparedProfileSubjectSQL)

    var i = 0
    for (profileId in profileIds) {
        val profileSubjectsAmount =
            Random.nextInt(PROFILE_SUBJECTS_AMOUNT.first, PROFILE_SUBJECTS_AMOUNT.second + 1)
        for (j in 0..profileSubjectsAmount) {
            logger.logId(i++, 10_000, tableName)

            val subjectId = subjectIds.random()
            with(profileSubjectBatch) {
                setInt(1, profileId)
                setInt(2, subjectId)
                setInt(3, profileId)
                setInt(4, subjectId)
                addBatch()
            }
        }
    }

    executeBatch(profileSubjectBatch, tableName)
}

fun insertDeadline(connection: Connection) {
    val tableName = "[DEADLINE]"
    logger.log("$tableName INSERTION STARTED")

    val studentList = fastIdSelecting(connection, "student")

    val deadlineBatch = connection.createStatement()
    val randomizer = DeadlineRandomizer()
    for (i in 0..GENERATED_DEADLINES_AMOUNT) {
        logger.logId(i, GENERATED_DEADLINES_AMOUNT / 10, tableName)

        val studentId = studentList.random()

        val deadline = randomizer.createNewDeadline(deadlineBatch)
        with(deadline) {
            deadlineBatch.addBatch(
                """
            INSERT INTO schema_schedule.deadline(title, description, notification_time, deadline_date, type, subject_id, lesson_id, media_url, student_id)
            SELECT '$title', '$description', '$notificationTime', '$date', '$type', $subjectID, $lessonID, '$mediaURL', $studentId
                WHERE NOT EXISTS(
                SELECT 1
                FROM schema_schedule.deadline
                WHERE title = '$title'
                AND description = '$description'
                AND notification_time = '$notificationTime'
                AND deadline_date = '$date'
                AND type = '$type'
                AND subject_id = $subjectID
                AND lesson_id = $lessonID
                AND media_url = '$mediaURL'
                AND student_id = $studentId
                )"""
            )
        }
    }

    executeBatch(deadlineBatch, tableName)
}

fun insertProgress(connection: Connection) {
    val tableName = "[PROGRESS]"
    logger.log("$tableName INSERTION STARTED")

    val progressBatch = connection.createStatement()
    val subjectIdQuery = connection.createStatement()
    val groupLevelQuery = connection.createStatement()

    val semesterResultSet = connection.createStatement().executeQuery("""
       SELECT id, level, student_id FROM schema_schedule.semester
    """)

    var i = 0
    while (semesterResultSet.next()) {
        val semesterId = semesterResultSet.getInt("id")
        val semesterLevel = semesterResultSet.getInt("level")
        val semesterStudentId = semesterResultSet.getInt("student_id")

        val groupLevelResultSet = groupLevelQuery.executeQuery("""
            SELECT level
            FROM schema_schedule."GROUP"
            WHERE id = (
                SELECT group_id
                FROM schema_schedule.student
                WHERE id = $semesterStudentId
            )
        """)


        val groupLevel: Int
        if (groupLevelResultSet.next()) {
            groupLevel = groupLevelResultSet.getInt("level")
        } else {
            continue
        }

        if (parsedFirstSemester && groupLevel * 2 - 1 != semesterLevel
            || parsedSecondSemester && groupLevel * 2 != semesterLevel) {
            continue
        }

        val subjectIdLessonDateResultSet = subjectIdQuery.executeQuery(
            """
                select subject_id, lesson_date
                from schema_schedule.lesson
                        inner join (
                    select lesson
                    FROM schema_schedule.lesson_group
                    where "GROUP" = (
                        select group_id
                        FROM schema_schedule.student
                        where id = $semesterStudentId
                    )
                ) as semester_lessons
                                    on lesson.id = semester_lessons.lesson
            """
        )

        while (subjectIdLessonDateResultSet.next()) {
            val lessonDate = subjectIdLessonDateResultSet.getDate("lesson_date").toLocalDate().month

            if (lessonDate in firstHalfMonths && parsedSecondSemester
                || lessonDate in secondHalfMonths && parsedFirstSemester) {
                continue
            }
            logger.logId(i++, 10_000, tableName)

            val subjectId = subjectIdLessonDateResultSet.getInt("subject_id")

            progressBatch.addBatch(
                """
                INSERT INTO schema_schedule.progress (semester_id, subject_id)
                SELECT $semesterId, $subjectId
                WHERE NOT EXISTS(
                    SELECT 1
                    FROM schema_schedule.progress
                    WHERE semester_id = $semesterId
                    AND subject_id = $subjectId
                )"""
            )
        }
    }

    executeBatch(progressBatch, tableName)
}

fun insertTask(connection: Connection) {
    val tableName = "[TASK]"
    logger.log("$tableName INSERTION STARTED")

    val taskBatch = connection.createStatement()

    val taskResultList = listOf("Зачёт", "Незачёт", "Отлично", "Хорошо", "Удовлетворительно", "Неудовлетворительно")

    val progressIdList = fastIdSelecting(connection, "progress")
    val taskTypeIdList = fastIdSelecting(connection, "task_type")

    var i = 0
    for (progressId in progressIdList) {
        val tasksForProgressAmount =
            Random.nextInt(TASK_FOR_EVERY_PROGRESS_AMOUNT.first, TASK_FOR_EVERY_PROGRESS_AMOUNT.second + 1)
        repeat(tasksForProgressAmount) {
            logger.logId(i++, progressIdList.size / 5, tableName)

            val taskTypeId = taskTypeIdList.random()

            val taskResult = if (taskTypeId == 1) {
                taskResultList[Random.nextInt(0, 2)]
            } else {
                taskResultList[Random.nextInt(2, taskResultList.size)]
            }

            taskBatch.addBatch(
                """
                INSERT INTO schema_schedule.task(progress_id, type_id, result)
                SELECT $progressId, $taskTypeId, '$taskResult'
                WHERE NOT EXISTS(
                    SELECT 1 
                    FROM schema_schedule.task
                    WHERE progress_id = $progressId
                    AND type_id = $taskTypeId
                    AND result = '$taskResult'
                )"""
            )
        }
    }

    executeBatch(taskBatch, tableName)
}

fun fastIdSelecting(connection: Connection, tableName: String): List<Int> {
    @Suppress("SqlResolve")
    val resultSet = connection.createStatement().executeQuery("""SELECT id FROM schema_schedule.$tableName""")
    val ids = mutableListOf<Int>()
    while (resultSet.next()) {
        ids.add(resultSet.getInt(1))
    }
    return ids
}
