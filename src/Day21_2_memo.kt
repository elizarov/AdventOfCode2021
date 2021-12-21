@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val dayId = "21"
    val input = readInput("Day${dayId}")
    val p = IntArray(2) { i -> input[i].removePrefix("Player ${i + 1} starting position: ").toInt() }
    data class WC(var w1: Long, var w2: Long)
    data class PS(val p1: Int, val p2: Int, val s1: Int, val s2: Int)
    val find = MemoizedRecursiveFunction<PS, WC> { (p1, p2, s1, s2) ->
        val c = WC(0, 0)
        for (d1 in 1..3) for (d2 in 1..3) for (d3 in 1..3) {
            val p1n = (p1 + d1 + d2 + d3 - 1) % 10 + 1
            val s1n = s1 + p1n
            if (s1n >= 21) {
                c.w1++
            } else {
                val cn = callRecursive(PS(p2, p1n, s2, s1n))
                c.w1 += cn.w2
                c.w2 += cn.w1
            }
        }
        c
    }
    val c = find(PS(p[0], p[1], 0, 0))
    println(maxOf(c.w1, c.w2))
}

@OptIn(ExperimentalStdlibApi::class)
fun <T, R> MemoizedRecursiveFunction(block: suspend DeepRecursiveScope<T,R>.(T) -> R): DeepRecursiveFunction<T, R> {
    val memo = HashMap<T,R>()
    return DeepRecursiveFunction { param ->
        if (param in memo) {
            @Suppress("UNCHECKED_CAST")
            memo[param] as R
        } else {
            block(param).also { memo[param] = it }
        }
    }
}
