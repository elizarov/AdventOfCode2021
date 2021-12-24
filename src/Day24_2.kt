import kotlin.time.*

fun main() {
    val start = TimeSource.Monotonic.markNow()
    val dayId = "24"
    val input = readInput("Day${dayId}").map { it.split(" ") }
    val n = input.size
    data class St(val w: Int, val x: Int, val y: Int, val z: Int) {
        operator fun get(r: String): Int = when (r) {
            "w" -> w
            "x" -> x
            "y" -> y
            "z" -> z
            else -> r.toInt()
        }
        fun set(r: String, v: Int): St = when (r) {
            "w" -> copy(w = v)
            "x" -> copy(x = v)
            "y" -> copy(y = v)
            "z" -> copy(z = v)
            else -> error("reg=$r")
        }
    }
    val dp = Array(n) { HashMap<St, Long>() }
    val pow = LongArray(n)
    var curPow = 1L
    for (i in n - 1 downTo 0) if (input[i][0] == "inp") {
        pow[i] = curPow
        curPow *= 10
    }
    fun find(i: Int, st: St): Long {
        if (i >= n) return if (st.z == 0) 0L else -1L
        dp[i][st]?.let { return it }
        val a = input[i][1]
        val b = input[i].getOrNull(2)
        val ans = when (input[i][0]) {
            "inp" -> {
                var res = -1L
                for (v in 1..9) {
                    val r = find(i + 1, st.set(a, v))
                    if (r >= 0) {
                        res = v * pow[i] + r
                        break
                    }
                }
                res
            }
            "add" -> find(i + 1, st.set(a, st[a] + st[b!!]))
            "mul" -> find(i + 1, st.set(a, st[a] * st[b!!]))
            "div" -> {
                val bv = st[b!!]
                if (bv == 0) -1L else find(i + 1, st.set(a, st[a] / bv))
            }
            "mod" -> {
                val av = st[a]
                val bv = st[b!!]
                if (av < 0 || bv <= 0) -1L else find(i + 1, st.set(a, av % bv))
            }
            "eql" -> find(i + 1, st.set(a, if (st[a] == st[b!!]) 1 else 0))
            else -> error("")
        }
        dp[i][st] = ans
        return ans
    }
    println(find(0, St(0, 0, 0, 0)))
    println("-- Scanned ${dp.sumOf { it.size }} states in ${start.elapsedNow()} --")
    for (i in 0 until n) {
        if (i == 0 || dp[i].size != dp[i - 1].size) println("$i: ${dp[i].size}")
    }
}

