fun main() {
    val dayId = "14"
    val input = readInput("Day${dayId}")
    var t = input[0]
    val r = input.drop(2)
        .map { it.split(" -> ") }
        .associateBy({ it[0] }, { it[1] })
    repeat(10) {
        t = buildString {
            for (i in 0 until t.length - 1) {
                val a = t.substring(i, i + 2)
                append(t[i])
                append(r[a]!!)
            }
            append(t.last())
        }
    }
    val l = t.groupingBy { it }.eachCount().map { it.value }
    val min = l.minOrNull()!!
    val max = l.maxOrNull()!!
    println(max - min)
}
