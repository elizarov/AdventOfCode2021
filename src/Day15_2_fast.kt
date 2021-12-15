import java.util.*

fun main() {
    val dayId = "15"
    val a0 = readInput("Day${dayId}").map { it.toCharArray().map { it.digitToInt() } }
    val n0 = a0.size
    val m0 = a0[0].size
    val n = 5 * n0
    val m = 5 * m0
    val a = Array(n) { i -> IntArray(m) { j ->
        val k = i / n0 + j / m0
        (a0[i % n0][j % m0] + k - 1) % 9 + 1
    } }
    val d = Array(n) { IntArray(m) { Int.MAX_VALUE } }
    data class Pos(val i: Int, val j: Int, val x: Int)
    val v = Array(n) { BooleanArray(m) }
    val q = PriorityQueue(compareBy(Pos::x))
    fun relax(i: Int, j: Int, x: Int) {
        if (i !in 0 until n || j !in 0 until m || v[i][j]) return
        val xx = x + a[i][j]
        if (xx < d[i][j]) {
            d[i][j] = xx
            q += Pos(i, j, xx)
        }
    }
    d[0][0] = 0
    q.add(Pos(0, 0, 0))
    while (!v[n - 1][m - 1]) {
        val (i, j, x) = q.remove()
        if (v[i][j]) continue
        v[i][j] = true
        relax(i - 1, j, x)
        relax(i + 1, j, x)
        relax(i, j - 1, x)
        relax(i, j + 1, x)
    }
    val ans = d[n - 1][m - 1]
    println(ans)
}
