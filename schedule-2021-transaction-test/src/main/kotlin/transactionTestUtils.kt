import RequestType.*
import TransactionType.*
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomLine
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.positionJitter
import org.intellij.lang.annotations.Language
import org.postgresql.ds.PGSimpleDataSource
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
private fun startTransaction(
    connection: Connection,
    requestType: RequestType,
    transactionType: TransactionType,
    requestAmount: Int,
    lastDeadlines: Int
) {
    val requestTypeSQL = requestType.name
    val transactionTypeSQL = transactionType.name
    val transactionStatement = connection.createStatement()

    logger.info("$transactionTypeSQL $requestTypeSQL REQUEST START")
    val duration: Duration = measureTime {
        for (i in 1..requestAmount) {
            if (i % (requestAmount / 5) == 0) {
                logger.info("$transactionTypeSQL $requestTypeSQL COUNT = $i")
            }

            @Language("PostgreSQL")
            val executableSQL = when (requestType) {
                SELECT -> """
                    START TRANSACTION ISOLATION LEVEL $transactionTypeSQL;
                    SELECT * FROM schema_schedule.deadline WHERE id > $lastDeadlines;
                    COMMIT;
                """
                INSERT -> """
                    START TRANSACTION ISOLATION LEVEL $transactionTypeSQL;
                    INSERT INTO schema_schedule.deadline
                    VALUES (DEFAULT, 'generated title', 'generated description',
                            (SELECT TIMESTAMP '2020-09-20 20:00:00' +
                                    RANDOM() * (TIMESTAMP '2022-09-01 20:00:00' -
                                                TIMESTAMP '2020-09-01 10:00:00'))::TIME,
                            (SELECT TIMESTAMP '2020-09-20 20:00:00' +
                                    RANDOM() * (TIMESTAMP '2022-09-01 20:00:00' -
                                                TIMESTAMP '2020-09-01 10:00:00'))::DATE,
                            (SELECT type from schema_schedule.deadline ORDER BY RANDOM() LIMIT 1),
                            (SELECT id FROM schema_schedule.subject ORDER BY RANDOM() LIMIT 1),
                            (SELECT id FROM schema_schedule.lesson ORDER BY RANDOM() LIMIT 1),
                            (SELECT media_url FROM schema_schedule.deadline ORDER BY RANDOM() LIMIT 1),
                            (SELECT id FROM schema_schedule.student ORDER BY RANDOM() LIMIT 1));
                    COMMIT;
                """
                UPDATE -> """
                    START TRANSACTION ISOLATION LEVEL $transactionTypeSQL;
                    UPDATE schema_schedule.deadline
                    SET title             = 'updated title $i',
                        description       = 'updated description $i',
                        notification_time = (SELECT TIMESTAMP '2020-09-20 20:00:00' +
                                                    RANDOM() * (TIMESTAMP '2022-09-01 20:00:00' -
                                                                TIMESTAMP '2020-09-01 10:00:00'))::TIME,
                        deadline_date     = (SELECT TIMESTAMP '2020-09-20 20:00:00' +
                                                    RANDOM() * (TIMESTAMP '2022-09-01 20:00:00' -
                                                                TIMESTAMP '2020-09-01 10:00:00'))::DATE,
                        type              = (SELECT type from schema_schedule.deadline ORDER BY RANDOM() LIMIT 1),
                        subject_id        = (SELECT id FROM schema_schedule.subject ORDER BY RANDOM() LIMIT 1),
                        lesson_id         = (SELECT id FROM schema_schedule.lesson ORDER BY RANDOM() LIMIT 1),
                        media_url         = (SELECT media_url FROM schema_schedule.deadline ORDER BY RANDOM() LIMIT 1),
                        student_id        = (SELECT id FROM schema_schedule.student ORDER BY RANDOM() LIMIT 1)
                    WHERE id = (SELECT id FROM deadline WHERE id > $lastDeadlines ORDER BY RANDOM() LIMIT 1);
                    COMMIT;
                """
            }

            transactionStatement.execute(executableSQL)
        }
    }

    logger.info("$transactionTypeSQL AVERAGE $requestTypeSQL REQUEST TIME ${duration / requestAmount}")
    logger.info("$transactionTypeSQL $requestTypeSQL REQUEST END")
}

@ExperimentalTime
private fun testTransactions(
    connection: Connection,
    executor: ExecutorService,
    transactionType: TransactionType,
    lastDeadlines: Int
) {
    val selectTransactionWorker = Runnable {
        startTransaction(connection, SELECT, transactionType, selectRequestsAmount, lastDeadlines)
    }

    val insertTransactionWorker = Runnable {
        startTransaction(connection, INSERT, transactionType, insertRequestsAmount, lastDeadlines)
    }

    val updateTransactionWorker = Runnable {
        startTransaction(connection, UPDATE, transactionType, updateRequestsAmount, lastDeadlines)
    }

    with(executor) {
        execute(selectTransactionWorker)
        execute(insertTransactionWorker)
        execute(updateTransactionWorker)
    }

    executor.shutdown()
}

@Suppress("ControlFlowWithEmptyBody")
@ExperimentalTime
fun testAllTransactions(connection: Connection) {
    // Фиксированное количество потоков, поэтому в конфиг файл выносить не нужно
    val threadsAmount = RequestType.values().size

    var lastDeadlines = 0

    if (configProperties.getProperty("config.isReadCommittedTestEnabled").toBoolean()) {
        val readCommittedExecutor = Executors.newFixedThreadPool(threadsAmount)
        testTransactions(connection, readCommittedExecutor, `READ COMMITTED`, lastDeadlines)
        while (!readCommittedExecutor.isTerminated) {
        }
        lastDeadlines += insertRequestsAmount

        logger.info("", "")
    }

    if (configProperties.getProperty("config.isRepeatableReadTestEnabled").toBoolean()) {
        val repeatableReadExecutor = Executors.newFixedThreadPool(threadsAmount)
        testTransactions(connection, repeatableReadExecutor, `REPEATABLE READ`, lastDeadlines)
        while (!repeatableReadExecutor.isTerminated) {
        }
        lastDeadlines += insertRequestsAmount

        logger.info("", "")
    }

    if (configProperties.getProperty("config.isSerializableTestEnabled").toBoolean()) {
        val serializableExecutor = Executors.newFixedThreadPool(threadsAmount)
        testTransactions(connection, serializableExecutor, SERIALIZABLE, lastDeadlines)
        while (!serializableExecutor.isTerminated) {
        }
    }

    logger.info("", "")
    logger.info("", "")
}
