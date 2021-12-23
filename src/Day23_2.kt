import java.util.*
import kotlin.collections.HashMap
import kotlin.math.*

fun main() {
    val dayId = "23"
    data class Cfg(val c: Array<CharArray>, val d: Int) {
        override fun equals(other: Any?): Boolean = other is Cfg && (0..4).all { i -> c[i].contentEquals(other.c[i]) }
        override fun hashCode(): Int = (0..4).fold(0) { a, i-> a * 31 + c[i].contentHashCode() }
        fun copy(d1: Int) = Cfg(c.map { it.copyOf() }.toTypedArray(), d1)
        override fun toString(): String = buildList {
            add("#".repeat(13))
            addAll(c.map { it.concatToString() })
            add("distance=$d")
        }.joinToString("\n")
    }
    val d = HashMap<Cfg,Int>()
    val q = PriorityQueue(compareBy(Cfg::d))
    val f = HashSet<Cfg>()
    fun enqueue(c: Cfg) {
        val d0 = d[c] ?: Int.MAX_VALUE
        if (c.d >= d0) return
        d[c] = c.d
        q += c
    }
    val input1 = readInput("Day${dayId}").subList(1, 4)
    val input2 = buildList<String> {
        add(input1[0])
        add(input1[1])
        add("  #D#C#B#A#  ")
        add("  #D#B#A#C#  ")
        add(input1[2])
    }
    val start = Cfg(input2.map { it.toCharArray() }.toTypedArray(), 0)
    println(start)
    enqueue(start)
    fun cost(c0: Char): Int = when(c0) {
        'A' -> 1
        'B' -> 10
        'C' -> 100
        'D' -> 1000
        else -> error("$c0")
    }
    while (true) {
        val c = q.remove()!!
        if (c in f) continue
        f += c
        val d0 = d[c]!!
        if (f.size % 10000 == 0) println("d=$d0, qs=${q.size}, fs=${f.size}")
        var ok = true
        check@for (r in 1..4) for (i in 0..3) if (c.c[r][2 * i + 3] != 'A' + i) {
            ok = false
            break@check
        }
        if (ok) {
            println(c)
            break
        }
        for (j0 in 1..11) {
            val c0 = c.c[0][j0]
            if (c0 !in 'A'..'D') continue
            val i = (c0 - 'A')
            val j1 = 2 * i + 3
            if (!(minOf(j0, j1) + 1..maxOf(j0, j1) - 1).all { j -> c.c[0][j] == '.' }) continue
            if (!(1..4).all { r -> c.c[r][j1] == '.' || c.c[r][j1] == c0 }) continue
            val r1 = (4 downTo 1).first { r -> c.c[r][j1] == '.' }
            val c1 = c.copy(d0 + cost(c0) * (abs(j1 - j0) + r1))
            c1.c[0][j0] = '.'
            c1.c[r1][j1] = c0
            enqueue(c1)
        }
        for (i in 0..3) for (r0 in 1..4) {
            val j0 = 2 * i + 3
            val c0 = c.c[r0][j0]
            if (c0 !in 'A'..'D') continue
            if (!(1..r0 - 1).all { r -> c.c[r][j0] == '.' }) continue
            for (j1 in 1..11) {
                if ((j1 - 3) % 2 == 0 && (j1 - 3) / 2 in 0..3) continue
                if (!(minOf(j1, j0)..maxOf(j1, j0)).all { j -> c.c[0][j] == '.' }) continue
                val c1 = c.copy(d0 + cost(c0) * (abs(j1 - j0) + r0))
                c1.c[r0][j0] = '.'
                c1.c[0][j1] = c0
                enqueue(c1)
            }
        }
    }
}
