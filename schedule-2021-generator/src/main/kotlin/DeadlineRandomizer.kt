import java.sql.Statement
import kotlin.random.Random

class DeadlineRandomizer {
    private var randomizer = 0

    fun createNewDeadline(statement: Statement): Deadline {
        randomizer = Random.nextInt(1, 10)
        val title = createTitle().replace("\'", "")
        val description = createDescription()
        val notificationTime = setNotificationTime()
        val date = setDate()
        val type = setType()
        val subjectID = setSubjectID(statement)
        val mediaURL = createMediaURL()
        val lessonID = setLessonID(statement)
        return Deadline(title, description, notificationTime, date, type, subjectID, mediaURL, lessonID)
    }

    private fun createTitle(): String {
        return when (randomizer) {
            1 -> "ModelSim lab deadline"
            2 -> "Math class postponed"
            3 -> "Do the test before sunday"
            4 -> "Teacher birthday today"
            5 -> "Jog in the morning"
            6 -> "OpenEdu test deadline"
            7 -> "Databases lab deadline"
            8 -> "English test deadline"
            9 -> "Physics lab deadline"
            else -> "Databases course project deadline"
        }
    }

    private fun createDescription(): String {
        return when (randomizer) {
            1 -> "Send a report and files for project of ModelSim lab to SPBSTU website before midnight"
            2 -> "Today math class is postponed due to teacher illness"
            3 -> "Need to do the test before sunday because of the meeting on this day"
            4 -> "Today is our teacher birthday, we need to congratulate him"
            5 -> "Will jog every morning to prepare for the physical education exam"
            6 -> "Should do the OpenEdu test before it expires"
            7 -> "Need to send report for database lab before 10am"
            8 -> "Should do the english class test on SPBSTU website before midnight"
            9 -> "Need to show results of physics lab to out teacher on today lesson"
            else -> "Should send a report of course project to our teacher before midnight"
        }
    }

    private fun setNotificationTime(): String {
        val randomHour = Random.nextInt(0, 23)
        val randomMinute = Random.nextInt(0, 59)
        val randomSecond = Random.nextInt(0, 59)
        return "$randomHour:$randomMinute:$randomSecond"
    }

    private fun setDate(): String {
        val randomDay = Random.nextInt(1, 30)
        val randomMonth = Random.nextInt(5, 6)
        var result = "2021-0$randomMonth-"
        if (randomDay < 10) result += "0$randomDay"
        else result += "$randomDay"
        return result
    }

    private fun setType(): String {
        return when (randomizer) {
            1 -> "Deadline"
            2 -> "Notification"
            3 -> "Task"
            4 -> "Reminder"
            5 -> "Goal"
            else -> "Deadline"
        }
    }

    private fun setSubjectID(statement: Statement): Int {
        val rs = statement.executeQuery(
            """
            SELECT id FROM schema_schedule.subject ORDER BY random() LIMIT 1;
        """.trimIndent()
        )
        rs.next()
        return rs.getInt(1)
    }

    private fun createMediaURL(): String? {
        val randomizer = Random.nextInt(0, 1000)
        return if (randomizer == 0) null
        else "https://ruz.spbstu.ru/$randomizer.html"
    }

    private fun setLessonID(statement: Statement): Int? {
        val rs = statement.executeQuery(
            """
            SELECT id FROM schema_schedule.lesson ORDER BY random() LIMIT 1;
        """.trimIndent()
        )
        if (rs.next()) {
            return rs.getInt(1)
        }
        return null
    }

}
