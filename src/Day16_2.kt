fun main() {
    val dayId = "16"
    val input = readInput("Day${dayId}")
    val s = input[0].map { it.digitToInt(16).toString(2).padStart(4, '0') }.joinToString("")
    var i = 0
    fun next(n: Int) = s.substring(i, i + n).toInt(2).also { i += n }
    fun parse(maxLen: Int = Int.MAX_VALUE, maxPackets: Int = Int.MAX_VALUE, type: Int = -1): Long {
        val start = i
        var numPackets = 0
        val res = ArrayList<Long>()
        while (++numPackets <= maxPackets && i - start < maxLen) {
            val v = next(3)
            print("[$v")
            val t = next(3)
            when (t) {
                4 -> {
                    var cur = 0L
                    while (next(1) == 1) cur = (cur shl 4) + next(4)
                    cur = (cur shl 4) + next(4)
                    res += cur
                }
                else -> when (next(1)) {
                    0 -> res += parse(maxLen = next(15), type = t)
                    1 -> res += parse(maxPackets = next(11), type = t)
                }
            }
            print("]")
        }
        return when(type) {
            -1 -> {
                require(res.size == 1)
                res[0]
            }
            0 -> res.sum()
            1 -> res.fold(1L, Long::times)
            2 -> res.minOrNull()!!
            3 -> res.maxOrNull()!!
            5 -> {
                require(res.size == 2)
                if (res[0] > res[1]) 1L else 0L
            }
            6 -> {
                require(res.size == 2)
                if (res[0] < res[1]) 1L else 0L
            }
            7 -> {
                require(res.size == 2)
                if (res[0] == res[1]) 1L else 0L
            }
            else -> error("type=$type")
        }
    }
    val ans = parse(maxPackets = 1)
    println()
    println(ans)
}
