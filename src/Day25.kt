fun main() {
    val dayId = "25"
    val a = readInput("Day${dayId}").map { it.toCharArray() }.toTypedArray()
    val n = a.size
    val m = a[0].size
    var step = 0
    val f = Array(n) { BooleanArray(m) }
    fun move(c: Char, di: Int, dj: Int): Boolean {
        var moved = false
        for (i in 0 until n) for (j in 0 until m) {
            f[i][j] = a[i][j] == c && a[(i + di) % n][(j + dj) % m] == '.'
            if (f[i][j]) moved = true
        }
        for (i in 0 until n) for (j in 0 until m) if (f[i][j]) {
            a[i][j] = '.'
            a[(i + di) % n][(j + dj) % m] = c
        }
        return moved
    }
    do {
        step++
        val moved1 = move('>', 0, 1)
        val moved2 = move('v', 1, 0)
    } while (moved1 || moved2)
    println(step)
}
