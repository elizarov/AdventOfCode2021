fun main() {
    val dayId = "08"
    val input = readInput("Day${dayId}")
    var ans = 0
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
        val scf = q[1]!!
        val sbd = q[4]!!.minus(scf)
        for (x in a.split(" ").map { it.toSet() }) {
            when (x.size) {
                6 -> when {
                    x.intersect(sbd).size == 1 -> q[0] = x
                    x.intersect(scf).size == 1 -> q[6] = x
                    else -> q[9] = x
                }
                5 -> when {
                    x.intersect(scf).size == 2 -> q[3] = x
                    x.intersect(sbd).size == 2 -> q[5] = x
                    else -> q[2] = x
                }
            }
        }
        var n = 0
        for (x0 in b.split(" ")) {
            val x = x0.toSet()
            val i = q.indexOf(x)
            check(i >= 0)
            n = 10 * n + i
        }
        ans += n
    }
    println(ans)
}
