fun main() {
    val dayId = "06"
    val input = readInput("Day${dayId}")
    val f = input[0].split(",").map { it.toInt() }
    var c = LongArray(9)
    for (x in f) c[x]++
    repeat(256) {
        val g = LongArray(9)
        for (x in 0..8) {
            when {
                x > 0 -> g[x - 1] += c[x]
                x == 0 -> {
                    g[6] += c[x]
                    g[8] += c[x]
                }
            }
        }
        c = g
    }
    println(c.sum())
}
