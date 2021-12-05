import kotlin.math.*

fun main() {
    val dayId = "05"
    val input = readInput("Day${dayId}")
    data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int)
    val a = input.map { l ->
        val s = l.split(" -> ")
        val (x1, y1) = s[0].split(",").map { it.toInt() }
        val (x2, y2) = s[1].split(",").map { it.toInt() }
        Line(x1, y1, x2, y2)
    }
    val mx = a.maxOf { l -> maxOf(l.x1, l.x2) }
    val my = a.maxOf { l -> maxOf(l.y1, l.y2) }
    val f = Array(mx + 1) { IntArray(my + 1) }
    for (l in a) {
        val dx = (l.x2 - l.x1).sign
        val dy = (l.y2 - l.y1).sign
        for (t in 0..maxOf((l.x1 - l.x2).absoluteValue, (l.y1 - l.y2).absoluteValue))
            f[l.x1 + t * dx][l.y1 + t * dy]++
    }
    var ans = 0
    for (x in 0..mx) for (y in 0..my) if (f[x][y] > 1) ans++
    println(ans)
}
