fun main() {
    val dayId = "21"
    val input = readInput("Day${dayId}")
    val p = IntArray(2)
    for (i in 0..1) {
        p[i] = input[i].removePrefix("Player ${i + 1} starting position: ").toInt()
    }
    data class WC(var w1: Long, var w2: Long)
    val dp = Array(11) { Array(11) { Array(21) { arrayOfNulls<WC>(21) } } }
    fun find(p1: Int, p2: Int, s1: Int, s2: Int): WC {
        dp[p1][p2][s1][s2]?.let { return it }
        val c = WC(0, 0)
        for (d1 in 1..3) for (d2 in 1..3) for (d3 in 1..3) {
            val p1n = (p1 + d1 + d2 + d3 - 1) % 10 + 1
            val s1n = s1 + p1n
            if (s1n >= 21) {
                c.w1++
            } else {
                val cn = find(p2, p1n, s2, s1n)
                c.w1 += cn.w2
                c.w2 += cn.w1
            }
        }
        dp[p1][p2][s1][s2] = c
        return c
    }
    val c = find(p[0], p[1], 0, 0)
    println(c)
    println(maxOf(c.w1, c.w2))
}
