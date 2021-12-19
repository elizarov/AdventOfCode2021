fun main() {
    val dayId = "14"
    val input = readInput("Day${dayId}")
    val t = input[0]
    val r = input.drop(2)
        .map { it.split(" -> ") }
        .associateBy({ it[0] }, { it[1] })
    var pc = t
        .windowed(2)
        .groupingBy { it }.eachCount()
        .mapValues { it.value.toLong() }
    repeat(40) {
        pc = pc.flatMap { (a, c) ->
            val b = r[a]!!
            listOf("${a[0]}$b" to c, "$b${a[1]}" to c)
        }.groupingBy { it.first }.fold(0L) { a, e -> a + e.second }
    }
    val ll =
        pc.toList()
        .flatMap { (p, c) -> listOf(p[0] to c, p[1] to c) }
        .groupingBy { it.first }.fold(0L) { a, e -> a + e.second }
        .toMutableMap()
    ll[t.first()] = ll[t.first()]!! + 1
    ll[t.last()] = ll[t.last()]!! + 1
    val l = ll.map { it.value / 2 }
    val min = l.minOrNull()!!
    val max = l.maxOrNull()!!
    println(max - min)
}
