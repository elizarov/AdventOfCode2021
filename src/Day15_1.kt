fun main() {
    val dayId = "15"
    val a = readInput("Day${dayId}").map { it.toCharArray().map { it.digitToInt() } }
    val n = a.size
    val m = a[0].size
    val d = Array(n) { IntArray(m) { Int.MAX_VALUE } }
    val v = Array(n) { BooleanArray(m) }
    fun relax(i: Int, j: Int, x: Int) {
        if (i !in 0 until n || j !in 0 until m) return
        d[i][j] = minOf(d[i][j], x + a[i][j])
    }
    d[0][0] = 0
    while (!v[n - 1][m - 1]) {
        var mx = Int.MAX_VALUE
        var mi = -1
        var mj = -1
        for (i in 0 until n) for (j in 0 until m) {
            if (!v[i][j] && d[i][j] < mx) {
                mx = d[i][j]
                mi = i
                mj = j
            }
        }
        v[mi][mj] = true
        relax(mi - 1, mj, mx)
        relax(mi + 1, mj, mx)
        relax(mi, mj - 1, mx)
        relax(mi, mj + 1, mx)
    }
    val ans = d[n - 1][m - 1]
    println(ans)
}
