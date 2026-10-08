package com.pippo2801.universalgamebot.learning

data class MemoryRecord(
    val timestamp: Long,
    val gameId: String,
    val stateSummary: String,
    val actionTaken: String,
    val result: String,
    val error: String? = null
)

class MemoryManager {
    private val memoryLog = mutableListOf<MemoryRecord>()

    fun record(gameId: String, stateSummary: String, actionTaken: String, result: String, error: String? = null) {
        memoryLog.add(MemoryRecord(System.currentTimeMillis(), gameId, stateSummary, actionTaken, result, error))
    }

    fun getRecords(gameId: String): List<MemoryRecord> {
        return memoryLog.filter { it.gameId == gameId }
    }
}
