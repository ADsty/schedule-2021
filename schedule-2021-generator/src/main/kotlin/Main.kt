import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun main() {
    val connection = createConnection() ?: return

    val duration = measureTime {
        insertAllTables(connection)
    }

    logger.log("====================================")
    logger.log(duration)
    logger.close()
}
