fun main() {
    val dayId = "22"
    val input = readInput("Day${dayId}")
    data class C(val m: Int, val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int)
    val a = input.map { s ->
        val (m1, s1) = s.split(" ")
        val m = when(m1) {
            "on" -> 1
            "off" -> 0
            else -> error("!!!")
        }
        val (xs, ys, zs) = s1.split(",")
        val (x1, x2) = xs.removePrefix("x=").split("..").map { it.toInt() }
        val (y1, y2) = ys.removePrefix("y=").split("..").map { it.toInt() }
        val (z1, z2) = zs.removePrefix("z=").split("..").map { it.toInt() }
        C(m, x1, x2, y1, y2, z1, z2)
    }
    val g = Array(101) { Array(101) { IntArray(101) } }
    for (c in a) if (c.x1 >= -50 && c.x2 <= 50) {
        for (x in c.x1..c.x2) for (y in c.y1..c.y2) for (z in c.z1..c.z2) {
            g[x + 50][y + 50][z + 50] = c.m
        }
    }
    val ans = g.sumOf { it.sumOf { it.sum() }}
    println(ans)
}
