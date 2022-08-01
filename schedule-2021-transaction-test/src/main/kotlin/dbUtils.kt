import org.postgresql.ds.PGSimpleDataSource
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.sql.Connection
import java.sql.SQLException
import java.util.*

fun createConnection(): Connection? {
    val connection: Connection?

    try {
        val ds = PGSimpleDataSource().apply {
            serverNames = arrayOf(configProperties.getProperty("db.serverName"))
            databaseName = configProperties.getProperty("db.databaseName")
            user = configProperties.getProperty("db.user")
            password = configProperties.getProperty("db.password")
        }

        connection = ds.connection.apply { schema = "schema_schedule" }
    } catch (e: SQLException) {
        println("Ошибка подключения базы данных\n")
        e.printStackTrace()

        return null
    }
    return connection
}

fun parseConfigProperties(): Properties {
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
