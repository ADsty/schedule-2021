import com.google.gson.annotations.SerializedName
import java.sql.Date

open class ScheduleApiDeserializable(
    val error: Boolean? = null
)

open class PlanApiDeserializable(
    val description: String? = null,
    val count: Int = 0
)

class Deadline(
    var title: String,
    val description: String,
    val notificationTime: String,
    val date: String,
    val type: String,
    val subjectID: Int,
    val mediaURL: String?,
    val lessonID: Int?
)

class Division(
    @SerializedName("id") val apiId: Int,
    val name: String,
    @SerializedName("valid_to") val closeDate: Date?,
    @SerializedName("parent") val parentId: Int
)

class ProfilesContainer(
    val next: String?,
    @SerializedName("results") val profileList: List<Profile>,
): PlanApiDeserializable()

class DivisionContainer(
    val next: String?,
    @SerializedName("results") val divisionList: List<Division>
): PlanApiDeserializable()

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
class Lesson(
    val subject: String,
    val type: Int,
    @SerializedName("time_start") val timeStart: String,
    @SerializedName("time_end") val timeEnd: String,
    @SerializedName("groups") val groupList: List<Group>,
    @SerializedName("auditories") val placeList: List<Place>
)

class Place(
    val name: String
)

class TeachersContainer(
    @SerializedName("teachers") val teacherList: List<Teacher>
) : ScheduleApiDeserializable()

class Teacher(
    @SerializedName("id") val apiId: Int,
    @SerializedName("full_name") val name: String
)

class FacultiesContainer(
    @SerializedName("faculties") val facultyList: List<Faculty>,
) : ScheduleApiDeserializable()

class Faculty(
    val name: String,
    @SerializedName("abbr") val short: String,
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
    @SerializedName("results") val subjectTypeList: List<SubjectType>,
) : PlanApiDeserializable()

class SubjectType(
    val name: String,
)
