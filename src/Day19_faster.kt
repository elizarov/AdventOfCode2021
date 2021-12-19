import kotlin.math.*
import kotlin.time.*

data class P3F(val x: Int, val y: Int, val z: Int)

fun main() {
    val start = TimeSource.Monotonic.markNow()
    val dayId = "19"
    val input = readInput("Day${dayId}")
    val a = ArrayList<HashSet<P3F>>()
    for (s in input) {
        if (s == "--- scanner ${a.size} ---") {
            a.add(HashSet())
            continue
        }
        if (s.isEmpty()) continue
        a.last().add(s.split(",").map { it.toInt() }.let { (x, y, z) -> P3F(x, y, z) })
    }
    val n = a.size
    val cf = Array(n) { BooleanArray(n) }
    val cr = Array(n) { arrayOfNulls<T3F.Ofs>(n) }
    fun check(ui: Int, vi: Int): T3F.Ofs? {
        if (cf[ui][vi]) return cr[ui][vi]
        cf[ui][vi] = true
        val u = a[ui]
        for (dir in 0 until 24) {
            val v = a[vi].map { it.rotate(dir) }
            val shs = buildSet { for (pu in u) for (pv in v) add(diff(pu, pv)) }
            for (sh in shs) {
                val cnt = v.map { it.shift(sh) }.count { u.contains(it) }
                if (cnt >= 12) return T3F.Ofs(dir, sh).also { cr[ui][vi] = it }
            }
        }
        return null
    }

    val t = arrayOfNulls<T3F>(n)
    val b = a[0].toMutableSet()
    val s = arrayOfNulls<P3F>(n)
    t[0] = T3F.Id
    s[0] = P3F(0, 0, 0)
    val found = hashSetOf(0)
    val rem = (1 until a.size).toHashSet()
    pair@while (rem.isNotEmpty()) {
        for (ui in found) for (vi in rem) {
            val o = check(ui, vi) ?: continue
            val f = T3F.Combo(o, t[ui]!!)
            t[vi] = f
            s[vi] = P3F(0, 0, 0).apply(f)
            b += a[vi].map { it.apply(f) }
            found += vi
            rem -= vi
            continue@pair
        }
        error("Cannot find")
    }
    println("Done in ${start.elapsedNow()}")
    println("part1 = ${b.size}")
    val part2 = buildList {
        for (s1 in s) for (s2 in s)
            add(diff(s1!!, s2!!).dist())
    }.maxOrNull()!!
    println("part2 = $part2")
}

sealed class T3F {
    object Id : T3F()
    class Ofs(val dir: Int, val sh: P3F) : T3F()
    class Combo(val t1: T3F, val t2: T3F) : T3F()
}

fun P3F.apply(t: T3F): P3F = when(t) {
    is T3F.Id -> this
    is T3F.Ofs -> rotate(t.dir).shift(t.sh)
    is T3F.Combo -> apply(t.t1).apply(t.t2)
}

fun diff(a: P3F, b: P3F): P3F = P3F(a.x - b.x, a.y - b.y, a.z - b.z)  

fun P3F.shift(b: P3F): P3F = P3F(this.x + b.x, this.y + b.y, this.z + b.z)

fun P3F.dist() = x.absoluteValue + y.absoluteValue + z.absoluteValue

fun P3F.get(i: Int) = when(i) {
    0 -> x
    1 -> y
    2 -> z
    else -> error("$i")
}

fun P3F.rotate(d: Int): P3F {
    val c0 = d % 3
    val c0s = 1 - ((d / 3) % 2) * 2
    val c1 = (c0 + 1 + (d / 6) % 2) % 3
    val c1s = 1 - (d / 12) * 2
    val c2 = 3 - c0 - c1
    val c2s = c0s * c1s * (if (c1 == (c0 + 1) % 3) 1 else -1)
    return P3F(get(c0) * c0s, get(c1) * c1s, get(c2) * c2s)
}
