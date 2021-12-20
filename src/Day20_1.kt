fun main() {
    val dayId = "20"
    val input = readInput("Day${dayId}")
    val z = input[0].map { if (it == '#') 0 else 1 }.toIntArray()
    check(z.size == 512)
    val inv = z[0] == 1
    var a = input.drop(2).map { it.map { if (it == '#') 1 else 0 }.toIntArray() }.toTypedArray()
    repeat(2) { round ->
        val n = a.size
        val m = a[0].size
        val b = Array(n + 2) { IntArray(m + 2) }
        val bg = if (inv) round % 2 else 0
        for (i in 0 until n + 2) for (j in 0 until m + 2) {
            var s = 0
            for (i1 in -1..1) for (j1 in -1..1)
                s = (s shl 1) + (a.getOrNull(i + i1 - 1)?.getOrNull(j + j1 - 1) ?: bg)
            b[i][j] = z[s]
        }
        a = b
    }
    println(a.sumOf { it.sum() })
}
