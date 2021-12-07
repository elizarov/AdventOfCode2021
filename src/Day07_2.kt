import kotlin.math.*

fun main() {
    val dayId = "07"
    val input = readInput("Day${dayId}")
    val a = input[0].split(",").map { it.toInt() }
    var best = Long.MAX_VALUE
    for (i in 0..a.maxOrNull()!!) {
        val f = a.sumOf { x ->
            val k = abs(i - x)
            k.toLong() * (k + 1) / 2
        }
        if (f < best) best = f
    }
    println(best)
}
