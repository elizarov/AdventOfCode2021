fun main() {
    val dayId = "02"
    val input = readInput("Day${dayId}")
    var d = 0
    var c = 0
    for (line in input) {
        val s = line.split(" ")
        val x = s[1].toInt()
        when(s[0]) {
            "forward" -> c += x
            "down" -> d += x
            "up" -> d -= x
        }
    }
    val ans = d * c
    println(ans)
}
