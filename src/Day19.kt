import kotlin.math.*
import kotlin.time.*

typealias P3 = List<Int>

fun main() {
    val start = TimeSource.Monotonic.markNow()
    val dayId = "19"
    val input = readInput("Day${dayId}")
    val a = ArrayList<HashSet<P3>>()
    for (s in input) {
        if (s == "--- scanner ${a.size} ---") {
            a.add(HashSet())
            continue
        }
        if (s.isEmpty()) continue
        a.last().add(s.split(",").map { it.toInt() })
    }
    val n = a.size
    val cf = Array(n) { BooleanArray(n) }
    val cr = Array(n) { arrayOfNulls<T3.Ofs>(n) }
    fun check(ui: Int, vi: Int): T3.Ofs? {
        if (cf[ui][vi]) return cr[ui][vi]
        cf[ui][vi] = true
        val u = a[ui]
        for (dir in 0 until 24) {
            val v = a[vi].map { it.rotate(dir) }
            val shs = buildSet { for (pu in u) for (pv in v) add(diff(pu, pv)) }
            for (sh in shs) {
                val cnt = v.map { it.shift(sh) }.count { u.contains(it) }
                if (cnt >= 12) return T3.Ofs(dir, sh).also { cr[ui][vi] = it }
            }
        }
        return null
    }

    val t = arrayOfNulls<T3>(n)
    val b = a[0].toMutableSet()
    val s = arrayOfNulls<P3>(n)
    t[0] = T3.Id
    s[0] = listOf(0, 0, 0)
    val found = hashSetOf(0)
    val rem = (1 until a.size).toHashSet()
    pair@while (rem.isNotEmpty()) {
        for (ui in found) for (vi in rem) {
            val o = check(ui, vi) ?: continue
            val f = T3.Combo(o, t[ui]!!)
            t[vi] = f
            s[vi] = listOf(0, 0, 0).apply(f)
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
            add(diff(s1!!, s2!!).sumOf { it.absoluteValue })
    }.maxOrNull()!!
    println("part2 = $part2")
}

sealed class T3 {
    object Id : T3()
    class Ofs(val dir: Int, val sh: P3) : T3()
    class Combo(val t1: T3, val t2: T3) : T3()
}

fun P3.apply(t: T3): P3 = when(t) {
    is T3.Id -> this
    is T3.Ofs -> rotate(t.dir).shift(t.sh)
    is T3.Combo -> apply(t.t1).apply(t.t2)
}

fun diff(a: P3, b: P3) = a.zip(b, Int::minus)

fun P3.shift(b: P3) = zip(b, Int::plus)

fun P3.rotate(d: Int): P3 {
    val c0 = d % 3
    val c0s = 1 - ((d / 3) % 2) * 2
    val c1 = (c0 + 1 + (d / 6) % 2) % 3
    val c1s = 1 - (d / 12) * 2
    val c2 = 3 - c0 - c1
    val c2s = c0s * c1s * (if (c1 == (c0 + 1) % 3) 1 else -1)
    return listOf(get(c0) * c0s, get(c1) * c1s, get(c2) * c2s)
}
