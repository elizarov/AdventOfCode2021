fun main() {
    val dayId = "03"
    val input = readInput("Day${dayId}")
    val n = input[0].length
    val a0 = IntArray(n)
    val a1 = IntArray(n)
    for (line in input) {
        for (i in line.indices) {
            when(line[i]) {
                '0' -> a0[i]++
                '1' -> a1[i]++
            }
        }
    }
    val g = CharArray(n) { '0' }
    val e = CharArray(n) { '0' }
    for (i in 0 until n) {
        if (a0[i] > a1[i]) e[i] = '1' else g[i] = '1'
    }
    val ans = g.concatToString().toInt(2) * e.concatToString().toInt(2)
    println(ans)
}
