fun main() {
    val dayId = "11"
    val input = readInput("Day${dayId}")
    val a = input.map { it.map { it.digitToInt() }.toMutableList() }
    val n = a.size
    val m = a[0].size
    var ans = 0
    data class Pos(val i: Int, val j: Int)
    repeat(100) {
        val q = ArrayList<Pos>()
        fun inc(i: Int, j: Int) {
            a[i][j]++
            if (a[i][j] == 10) {
                q += Pos(i, j)
            }
        }
        for (i in 0 until n) for (j in 0 until m) inc(i, j)
        var qh = 0
        while (qh < q.size) {
            val (i, j) = q[qh++]
            for (di in -1..1) for(dj in -1..1) if (di != 0 || dj != 0) {
                val i1 = i + di
                val j1 = j + dj
                if (i1 in 0 until n && j1 in 0 until m) inc(i1, j1)
            }
        }
        ans += q.size
        for (p in q) {
            a[p.i][p.j] = 0
        }
    }
    println(ans)
}
