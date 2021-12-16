fun main() {
    val dayId = "16"
    val input = readInput("Day${dayId}")
    val s = input[0].map { it.digitToInt(16).toString(2).padStart(4, '0') }.joinToString("")
    var i = 0
    fun next(n: Int) = s.substring(i, i + n).toInt(2).also { i += n }
    var ans = 0
    fun parse(maxLen: Int = Int.MAX_VALUE, maxPackets: Int = Int.MAX_VALUE) {
        val start = i
        var numPackets = 0
        while (++numPackets <= maxPackets && i - start < maxLen) {
            val v = next(3)
            ans += v
            print("[$v")
            when (next(3)) {
                4 -> {
                    while (next(1) == 1) next(4)
                    next(4)
                }
                else -> when (next(1)) {
                    0 -> parse(maxLen = next(15))
                    1 -> parse(maxPackets = next(11))
                }
            }
            print("]")
        }
    }
    parse(maxPackets = 1)
    println()
    println(ans)
}
