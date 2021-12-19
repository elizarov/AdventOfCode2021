import kotlin.math.*
import kotlin.time.*

data class P3D(val x: Int, val y: Int, val z: Int)

fun main() {
    val start = TimeSource.Monotonic.markNow()
    val dayId = "19"
    val input = readInput("Day${dayId}")
    val a = ArrayList<HashSet<P3D>>()
    for (s in input) {
        if (s == "--- scanner ${a.size} ---") {
            a.add(HashSet())
            continue
        }
        if (s.isEmpty()) continue
        a.last().add(s.split(",").map { it.toInt() }.let { (x, y, z) -> P3D(x, y, z) })
    }
    val n = a.size
    fun check(ui: Int, vi: Int): T3D? {
        val u = a[ui]
        for (dir in 0 until 24) {
            val v = a[vi].map { it.rotate(dir) }
            val sh = sequence { for (pu in u) for (pv in v) yield(diff(pu, pv)) }
                .groupingBy { it }
                .eachCount()
                .filterValues { it >= 12 }
                .keys.firstOrNull() ?: continue
            return T3D(dir, sh)
        }
        return null
    }

    val t = arrayOfNulls<List<T3D>>(n)
    val b = a[0].toMutableSet()
    val s = arrayOfNulls<P3D>(n)
    t[0] = emptyList()
    s[0] = P3D(0, 0, 0)
    val found = ArrayDeque<Int>().apply { add(0) }
    val rem = (1 until a.size).toHashSet()
    while (rem.isNotEmpty()) {
        val ui = found.removeFirst()
        for (vi in rem.toList()) {
            val o = check(ui, vi) ?: continue
            val f = listOf(o) + t[ui]!!
            t[vi] = f
            s[vi] = P3D(0, 0, 0).apply(f)
            b += a[vi].map { it.apply(f) }
            found += vi
            rem -= vi
        }
    }
    println("Done in ${start.elapsedNow()}")
    println("part1 = ${b.size}")
    val part2 = buildList {
        for (s1 in s) for (s2 in s)
            add(diff(s1!!, s2!!).dist())
    }.maxOrNull()!!
    println("part2 = $part2")
}

class T3D(val dir: Int, val sh: P3D)

fun P3D.apply(t: T3D): P3D = rotate(t.dir).shift(t.sh)
fun P3D.apply(t: List<T3D>): P3D = t.fold(this, P3D::apply)

fun diff(a: P3D, b: P3D): P3D = P3D(a.x - b.x, a.y - b.y, a.z - b.z)

fun P3D.shift(b: P3D): P3D = P3D(this.x + b.x, this.y + b.y, this.z + b.z)

fun P3D.dist() = x.absoluteValue + y.absoluteValue + z.absoluteValue

fun P3D.get(i: Int) = when(i) {
    0 -> x
    1 -> y
    2 -> z
    else -> error("$i")
}

fun P3D.rotate(d: Int): P3D {
    val c0 = d % 3
    val c0s = 1 - ((d / 3) % 2) * 2
    val c1 = (c0 + 1 + (d / 6) % 2) % 3
    val c1s = 1 - (d / 12) * 2
    val c2 = 3 - c0 - c1
    val c2s = c0s * c1s * (if (c1 == (c0 + 1) % 3) 1 else -1)
    return P3D(get(c0) * c0s, get(c1) * c1s, get(c2) * c2s)
}
