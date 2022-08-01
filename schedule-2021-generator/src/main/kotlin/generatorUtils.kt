import okhttp3.Request
import okhttp3.Response
import org.postgresql.Driver
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import java.util.Properties

fun createConnection(): Connection? {
    val connection: Connection?

    try {
        val driver = Driver()
        DriverManager.registerDriver(driver)

        connection = DriverManager.getConnection(DB_URL, DB_PROPERTIES)
    } catch (e: SQLException) {
        if (DEV_MODE) {
            println("Ошибка подключения базы данных\n")
            e.printStackTrace()
        }
        return null
    }
    return connection
}

fun getApiResponse(url: String, isPlanApiUsed: Boolean = false): String? {
    val request = Request.Builder().url(url).apply {
        if (isPlanApiUsed) {
            addHeader("X-Studyplan-Repository-Api-Key", PLAN_API_KEY)
        }
    }.build()

    val response: Response

    try {
        response = httpClient.newCall(request).execute()
    } catch (e: IOException) {
        if (DEV_MODE) {
            e.printStackTrace()
        }
        return null
    }

    return response.body?.string()
}

fun isPlanResponseCorrect(container: PlanApiDeserializable, tableName: String): Boolean {
    val check = container.description == "Неправильная страница" || container.count == 0
    if (DEV_MODE && check) {
        println("$tableName Неправильная страница")
    }
    return check
}

fun PrintWriter.log(message: Any) {
    this.write(message.toString())
    this.println()
    this.flush()
}

fun PrintWriter.logId(value: Int, delimiter: Int, tableName: String) {
    if (value % (delimiter) == 0) {
            this.log("$tableName ID = $value")
    }
}

fun executeBatch(batch: Statement, tableName: String, isInsertionFinish: Boolean = true) {
    try {
        batch.executeBatch()
    } catch (e: SQLException) {
        if (DEV_MODE) {
            logger.log("$tableName executeBatch Exception")
            e.printStackTrace()
        }
        return
    }

    if (isInsertionFinish) {
        logger.log("$tableName INSERTION FINISHED")
    }
}

fun parseGeneratorProperties(): Properties {
    val prop = Properties()
    val dbFileName = "./src/main/resources/db.properties"
    val configFileName = "./src/main/resources/config.properties"

    val dbInpStream: InputStream
    val configInpStream: InputStream

    try {
        dbInpStream = FileInputStream(dbFileName)
    } catch (e: FileNotFoundException) {
        println("File $dbFileName not found")
        throw e
    }
    try {
        configInpStream = FileInputStream(configFileName)
    } catch (e: FileNotFoundException) {
        println("File $configFileName not found")
        throw e
    }

    try {
        prop.load(dbInpStream)
    } catch (e: IOException) {
        println("Configuration from $dbFileName not loaded")
        throw e
    }
    try {
        prop.load(configInpStream)
    } catch (e: IOException) {
        println("Configuration from $configFileName not loaded")
        throw e
    }

    return prop
}
