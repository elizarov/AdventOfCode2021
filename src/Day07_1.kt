import kotlin.math.*

fun main() {
    val dayId = "07"
    val input = readInput("Day${dayId}")
    val a = input[0].split(",").map { it.toInt() }
    var best = Int.MAX_VALUE
    for (i in 0..a.maxOrNull()!!) {
        val f = a.sumOf { x -> abs(i - x) }
        if (f < best) best = f
    }
    println(best)
}
