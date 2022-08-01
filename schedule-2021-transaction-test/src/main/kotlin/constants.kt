import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

val configProperties = parseConfigProperties()

val isTestTransactionsEnabled = configProperties.getProperty("config.isTestTransactionsEnabled").toBoolean()
val selectRequestsAmount = configProperties.getProperty("config.selectRequestsAmount").toInt()
val insertRequestsAmount = configProperties.getProperty("config.insertRequestsAmount").toInt()
val updateRequestsAmount = configProperties.getProperty("config.updateRequestsAmount").toInt()

val logger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)

@Suppress("EnumEntryName")
enum class TransactionType {
    `READ COMMITTED`,
    `REPEATABLE READ`,
    SERIALIZABLE
}

enum class RequestType(val requestsAmount: Int) {
    SELECT(selectRequestsAmount),
    INSERT(insertRequestsAmount),
    UPDATE(updateRequestsAmount)
}

fun String.toTransactionType(): TransactionType {
    return when (this) {
        TransactionType.`READ COMMITTED`.name -> TransactionType.`READ COMMITTED`
        TransactionType.`REPEATABLE READ`.name -> TransactionType.`REPEATABLE READ`
        TransactionType.SERIALIZABLE.name -> TransactionType.SERIALIZABLE
        else -> throw IllegalArgumentException("WRONG TRANSACTION TYPE IN LOG FILE")
    }
}


fun String.toRequestType(): RequestType {
    return when (this) {
        RequestType.SELECT.name -> RequestType.SELECT
        RequestType.INSERT.name -> RequestType.INSERT
        RequestType.UPDATE.name -> RequestType.UPDATE
        else -> throw IllegalArgumentException("WRONG REQUEST TYPE IN LOG FILE")
    }
}
