import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.serpro69.kfaker.Faker
import okhttp3.OkHttpClient
import java.io.PrintWriter
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val generatorProperties = parseGeneratorProperties()

val DEV_MODE = generatorProperties.getProperty("dev_mode").toBoolean()

val DB_URL: String = generatorProperties.getProperty("db.url")

val DB_PROPERTIES = Properties().apply {
    put("user", generatorProperties.getProperty("db.login"))
    put("password", generatorProperties.getProperty("db.password"))
}

val PLAN_API_KEY: String = generatorProperties.getProperty("db.apikey")

class ApiURL {
    companion object {
        val FACULTIES: String = generatorProperties.getProperty("api.faculties")
        val GROUP: String = generatorProperties.getProperty("api.groups")
        val TEACHERS: String = generatorProperties.getProperty("api.teachers")
        val SCHEDULER: String = generatorProperties.getProperty("api.scheduler")
        val PROFILE: String = generatorProperties.getProperty("api.profile")
        val SUBTYPE: String = generatorProperties.getProperty("api.subject_type")
        val DEPARTMENT: String = generatorProperties.getProperty("api.department")
    }
}

val gson: Gson = GsonBuilder()
    .serializeNulls()
    .setDateFormat(DateFormat.SHORT, DateFormat.SHORT)
    .create()

val httpClient = OkHttpClient()
    .newBuilder()
    .connectTimeout(1, TimeUnit.HOURS)
    .readTimeout(1, TimeUnit.HOURS)
    .build()

val faker = Faker()

val logger = PrintWriter("generation.log")
