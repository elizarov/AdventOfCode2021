fun main() {
    val dayId = "08"
    val input = readInput("Day${dayId}")
    val ans = IntArray(11)
    for (s in input) {
        val (a, b) = s.split(" | ")
        val q = arrayOfNulls<String>(11)
        for (x0 in a.split(" ")) {
            val x = x0.toCharArray().sorted().joinToString("")
            when (x.length) {
                2 -> q[1] = x
                4 -> q[4] = x
                3 -> q[7] = x
                7 -> q[8] = x
            }
        }
        for (x0 in b.split(" ")) {
            val x = x0.toCharArray().sorted().joinToString("")
            val i = q.indexOf(x)
            if (i >= 0) ans[i]++
        }
    }
    println(ans.sum())
}
