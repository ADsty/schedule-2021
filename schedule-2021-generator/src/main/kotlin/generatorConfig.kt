import java.time.LocalDate
import java.time.Month

val GENERATED_STUDENT_AMOUNT =
    generatorProperties.getProperty("generated_student_amount").toInt()

val SCHEDULER_DATE_START: String =
    generatorProperties.getProperty("scheduler_date_start")

val SCHEDULER_DATE_END: String =
    generatorProperties.getProperty("scheduler_date_end")

val SCHEDULER_PARSE_ONLY_FIRST_WEEK =
    generatorProperties.getProperty("scheduler_parse_only_first_week").toBoolean()

val firstHalfMonths = listOf(Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER, Month.JANUARY)
val secondHalfMonths = listOf(Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST)

val dateStartMonth: Month = LocalDate.parse(SCHEDULER_DATE_START).month
val dateEndMonth: Month = LocalDate.parse(SCHEDULER_DATE_END).month

val parsedFirstSemester = dateStartMonth in firstHalfMonths && dateEndMonth in firstHalfMonths
val parsedSecondSemester = dateStartMonth in secondHalfMonths && dateEndMonth in secondHalfMonths
val parsedBothSemesters =
    if (dateStartMonth in firstHalfMonths && dateEndMonth in secondHalfMonths) {
        throw IllegalArgumentException("For correct semesters insertion only one semester should be parsed")
    } else {
        false
    }


val GENERATED_DEADLINES_AMOUNT =
    generatorProperties.getProperty("generated_deadlines_amount").toInt()

val PROFILE_SUBJECTS_AMOUNT =
    Pair(
        generatorProperties.getProperty("profile_subject_amount.min").toInt(),
        generatorProperties.getProperty("profile_subject_amount.max").toInt()
    )

val TASK_FOR_EVERY_PROGRESS_AMOUNT =
    Pair(
        generatorProperties.getProperty("task_for_every_progress_amount.min").toInt(),
        generatorProperties.getProperty("task_for_every_progress_amount.max").toInt()
    )
