fun main() {
    val dayId = "21"
    val input = readInput("Day${dayId}")
    val p = IntArray(2)
    for (i in 0..1) {
        p[i] = input[i].removePrefix("Player ${i + 1} starting position: ").toInt()
    }
    var rolls = 0L
    val s = IntArray(2)
    var i = 0
    var d = 1
    fun next(): Int {
        rolls++
        return d.also { d = d % 100 + 1 }
    }
    while (true) {
        p[i] = (p[i] + next() + next() + next() - 1) % 10 + 1
        s[i] += p[i]
        if (s[i] >= 1000) {
            println(s[1 - i] * rolls)
            break
        }
        i = (i + 1) % 2
    }
}
