fun main() {
    val dayId = "02"
    val input = readInput("Day${dayId}")
    var d = 0
    var c = 0
    var a = 0
    for (line in input) {
        val s = line.split(" ")
        val x = s[1].toInt()
        when(s[0]) {
            "forward" -> {
                c += x
                d += a * x
            }
            "down" -> { a += x }
            "up" -> { a -= x }
        }
    }
    val ans = d * c
    println(ans)
}
