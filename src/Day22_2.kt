fun main() {
    val dayId = "22"
    val input = readInput("Day${dayId}")
    data class Cube(val m: Boolean, val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int)
    val a = input.map { s ->
        val (m, s1) = s.split(" ")
        val (xs, ys, zs) = s1.split(",")
        val (x1, x2) = xs.removePrefix("x=").split("..").map { it.toInt() }
        val (y1, y2) = ys.removePrefix("y=").split("..").map { it.toInt() }
        val (z1, z2) = zs.removePrefix("z=").split("..").map { it.toInt() }
        Cube(m == "on", x1, x2 + 1, y1, y2 + 1, z1, z2 + 1)
    }
    val ux = a.flatMap { listOf(it.x1, it.x2) }.distinct().sorted()
    val uy = a.flatMap { listOf(it.y1, it.y2) }.distinct().sorted()
    val uz = a.flatMap { listOf(it.z1, it.z2) }.distinct().sorted()
    val g = Array(ux.size) { Array(uy.size) { BooleanArray(uz.size) } }
    val mx = ux.withIndex().associateBy({ it.value }, { it.index })
    val my = uy.withIndex().associateBy({ it.value }, { it.index })
    val mz = uz.withIndex().associateBy({ it.value }, { it.index })
    for (c in a) {
        for (x in mx[c.x1]!! until mx[c.x2]!!)
            for (y in my[c.y1]!! until my[c.y2]!!)
                for (z in mz[c.z1]!! until mz[c.z2]!!) {
                    g[x][y][z] = c.m
                }
    }
    var ans = 0L
    for (x in 0 until ux.size - 1) for (y in 0 until uy.size - 1) for (z in 0 until uz.size - 1) {
        if (g[x][y][z]) ans += (ux[x + 1] - ux[x]).toLong() * (uy[y + 1] - uy[y]) * (uz[z + 1] - uz[z])
    }
    println(ans)
}
