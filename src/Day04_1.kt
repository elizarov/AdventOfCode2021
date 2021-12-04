fun main() {
    val dayId = "04"
    val input = readInput("Day${dayId}")
    val nums = input[0].split(",").map { it.toInt() }
    class Brd(ls: List<String>) {
        val a = ls.map { l -> l.trim().split(" ").filter { it != "" }.map { it.toInt() } }
        val m = Array(5) { BooleanArray(5) }
        val r = IntArray(5)
        val c = IntArray(5)

        fun mark(x: Int): Boolean {
            for (i in 0..4) for (j in 0..4) {
                if (a[i][j] == x && !m[i][j]) {
                    m[i][j] = true
                    r[i]++
                    c[j]++
                }
            }
            return r.any { it == 5 } || c.any { it == 5 }
        }

        fun score(): Int {
            var s = 0
            for (i in 0..4) for (j in 0..4) {
                if (!m[i][j]) s += a[i][j]
            }
            return s
        }
    }
    val bs = ArrayList<Brd>()
    for (r in 2 until input.size step 6) {
        bs += Brd(input.subList(r, r + 5))
    }
    var ans = 0
    x@for (x in nums) {
        for (b in bs) {
            if (b.mark(x)) {
                ans = x * b.score()
                break@x
            }
        }
    }
    println(ans)
}
