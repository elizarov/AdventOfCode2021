fun main() {
    val dayId = "09"
    val a = readInput("Day${dayId}").map { it.toList() }
    val b = ArrayList<Int>()
    val n = a.size
    val m = a[0].size
    val u = Array(n) { BooleanArray(m) }
    fun dfs(i: Int, j: Int): Int {
        if (i < 0 || i >= n || j < 0 || j >= m || u[i][j] || a[i][j] == '9') return 0
        u[i][j] = true
        return 1 + dfs(i + 1, j) + dfs(i - 1, j) + dfs(i, j + 1) + dfs(i, j - 1)
    }
    for (i in 0 until n) {
        for (j in 0 until m) {
            if (!u[i][j] && a[i][j] != '9') {
                b += dfs(i, j)
            }
        }
    }
    b.sortDescending()
    println(b[0] * b[1] * b[2])
}
