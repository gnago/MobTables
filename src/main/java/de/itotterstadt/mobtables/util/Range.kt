package de.itotterstadt.mobtables.util

fun range(range: Any): Int {
    if (range is Int) {
        return range
    }
    val (min, max) = if (range is String) {
        val parts = range.split("..")
        val min = parts[0].toInt()
        val max = parts[1].toInt()
        Pair(min, max)
    } else {
        val map = range as LinkedHashMap<*, *>
        val min = map["from"] as Int
        val max = map["to"] as Int
        Pair(min, max)
    }

    val diff = max - min
    val off = Math.min(Math.floor(Math.random() * (diff + 1)).toInt(), diff)
    return min + off
}