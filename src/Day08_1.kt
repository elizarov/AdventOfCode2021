fun main() {
    val dayId = "08"
    val input = readInput("Day${dayId}")
    val ans = IntArray(11)
    for (s in input) {
        val (a, b) = s.split(" | ")
        val q = arrayOfNulls<Set<Char>>(11)
        for (x in a.split(" ").map { it.toSet() }) {
            when (x.size) {
                2 -> q[1] = x
                4 -> q[4] = x
                3 -> q[7] = x
                7 -> q[8] = x
            }
        }
        for (x in b.split(" ").map { it.toSet() }) {
            val i = q.indexOf(x)
            if (i >= 0) ans[i]++
        }
    }
    println(ans.sum())
}
