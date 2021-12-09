fun main() {
    val dayId = "09"
    val a = readInput("Day${dayId}").map { it.toList() }
    var ans = 0
    val n = a.size
    val m = a[0].size
    for (i in 0 until n) {
        for (j in 0 until m) {
            val b = listOfNotNull(
                a[i].getOrNull(j + 1),
                a[i].getOrNull(j - 1),
                a.getOrNull(i - 1)?.get(j),
                a.getOrNull(i + 1)?.get(j),
            )
            if (a[i][j] < b.minOrNull()!!) ans += 1 + (a[i][j] - '0')
        }
    }
    println(ans)
}
