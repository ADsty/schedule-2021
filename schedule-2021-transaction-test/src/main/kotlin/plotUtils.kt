import RequestType.*
import TransactionType.*
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomLine
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.label.ggtitle
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.positionJitter
import java.io.File

private fun createRequestMaps(): Map<RequestType, MutableList<Pair<TransactionType, Double>>> {
    val requestMaps = mutableMapOf<RequestType, MutableList<Pair<TransactionType, Double>>>()
    for (requestType in RequestType.values()) {
        requestMaps[requestType] = mutableListOf()
    }
    return requestMaps.toMap()
}

fun createPlots() {
    val axisY = "Среднее время запроса, c"
    val resultsGroups = "Результаты вычислений"

    val requestMaps = createRequestMaps()

    val transactionLogText = File("transaction.log").readText()

    val requestTypeRegex = "${`READ COMMITTED`.name}|${`REPEATABLE READ`.name}|${SERIALIZABLE.name}"
    val transactionTypeRegex = "${SELECT.name}|${INSERT.name}|${UPDATE.name}"

    """($requestTypeRegex) AVERAGE ($transactionTypeRegex) \w+ \w+ ([\d.]+)(?=ms|s)"""
        .toRegex()
        .findAll(transactionLogText)
        .map { it.groupValues }
        .forEach { groups ->
            val transactionType = groups[1].toTransactionType()
            val reqType = groups[2].toRequestType()
            val averageTime = groups[3].toDouble()
            requestMaps[reqType]!!.add(transactionType to averageTime)
        }

    for (requestType in RequestType.values()) {
        val axisX = "Уровень изоляции"

        val dataPoints = mapOf(
            resultsGroups to List(requestMaps[requestType]?.map { it.first.name }!!.size) { "Время запроса" },
            axisY to requestMaps[requestType]?.map { it.second },
            axisX to requestMaps[requestType]?.map { it.first.name }
        )

        val avgReadComm = requestMaps[requestType]!!.filter { it.first == `READ COMMITTED` }.map { it.second }.average()
        val avgRepRead = requestMaps[requestType]!!.filter { it.first == `REPEATABLE READ` }.map { it.second }.average()
        val avgSer = requestMaps[requestType]!!.filter { it.first == SERIALIZABLE }.map { it.second }.average()

        val dataAverage = mapOf(
            resultsGroups to List(3) { "Среднее по всем значениям" },
            axisY to listOf(avgReadComm, avgRepRead, avgSer),
            axisX to listOf(`READ COMMITTED`.name, `REPEATABLE READ`.name, SERIALIZABLE.name)
        )

        var plot = letsPlot(dataPoints) { x = axisX; y = axisY; color = resultsGroups }
        plot += ggtitle("Тип запроса - ${requestType.name}, количество запросов - ${requestType.requestsAmount}")
        plot += geomPoint(data = dataPoints, shape = 1, size = 4, position = positionJitter(width = 0.1))
        plot += geomPoint(data = dataAverage, size = 6)
        plot += geomLine(dataAverage)
        plot += ggsize(1000, 500)

        ggsave(
            plot,
            requestType.name.toLowerCase() +
                if (configProperties.getProperty("config.isSvgPlots").toBoolean()) {
                    ".svg"
                } else {
                    ".png"
                }
        )
    }
}
