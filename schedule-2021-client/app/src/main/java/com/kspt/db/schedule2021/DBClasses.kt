package com.kspt.db.schedule2021

import com.google.gson.annotations.SerializedName
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

open class ScheduleApiDeserializable(
    val error: Boolean? = null
)

open class PlanApiDeserializable(
    val description: String? = null,
    val count: Int = 0
)

data class Deadline(
    val id: Long,
    var title: String,
    var subjectName: String,
    val description: String,
    val notificationTime: String,
    val deadlineDate: String,
    val type: String,
    val lesson: Lesson
) {
    fun dateToCalendarDay(): CalendarDay {
        val parsedDate = deadlineDate.split("-")
        return CalendarDay.from(parsedDate[0].toInt(), parsedDate[1].toInt(), parsedDate[2].toInt())
    }
}

class Division(
    @SerializedName("id") val apiId: Int,
    val name: String,
    @SerializedName("valid_to") val closeDate: Date?,
    @SerializedName("parent") val parentId: Int
)

class ProfilesContainer(
    val next: String?,
    @SerializedName("results") val profileList: List<Profile>
) : PlanApiDeserializable()

class DivisionContainer(
    val next: String?,
    @SerializedName("results") val divisionList: List<Division>
) : PlanApiDeserializable()

class Profile(
    val name: String,
    val code: String
)

class Scheduler(
    @SerializedName("days") val dayList: List<Day>
) : ScheduleApiDeserializable()

class Day(
    val date: Date,
    @SerializedName("lessons") val lessonList: List<Lesson>
)

@Suppress("SpellCheckingInspection")
data class Lesson(
    val subjectName: String,
    val lessonDate: String,
    val type: String,
    val timeStart: String,
    val timeEnd: String,
    val teacherList: List<Teacher>,
    val place: String
)

class Place(
    val name: String
)

class TeachersContainer(
    @SerializedName("teachers") val teacherList: List<Teacher>
) : ScheduleApiDeserializable()

class Teacher(
    val id: Int,
    val name: String
)

class FacultiesContainer(
    @SerializedName("faculties") val facultyList: List<Faculty>
) : ScheduleApiDeserializable()

class Faculty(
    val name: String,
    @SerializedName("abbr") val short: String
)

class GroupContainer(
    @SerializedName("groups") val groupList: List<Group>
) : ScheduleApiDeserializable()

class Group(
    val name: String,
    val level: Int,
    val type: String,
    @SerializedName("kind") val origin: Int,
    val spec: String,
    val year: Int,
    @SerializedName("id") val api_id: Int
)

class SubjectTypeContainer(
    val next: String?,
    @SerializedName("results") val subjectTypeList: List<SubjectType>
) : PlanApiDeserializable()

class SubjectType(
    val name: String
)

class Subject(
    val name: String
)

class JWTContainer(
    val jwt: String
) : ScheduleApiDeserializable()
