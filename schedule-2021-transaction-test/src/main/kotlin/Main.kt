import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {

    if (isTestTransactionsEnabled) {
        val connection = createConnection() ?: return

        repeat(configProperties.getProperty("config.fullTestsAmount").toInt()) {
            testAllTransactions(connection)
        }
    } else {
        createPlots()
    }
}
