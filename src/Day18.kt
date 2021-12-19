sealed class SNum {
    class Reg(val x: Int) : SNum() { override fun toString(): String = x.toString() }
    class Pair(val l: SNum, val r: SNum) : SNum() { override fun toString(): String = "[$l,$r]" }
}

fun parseSNum(s: String): SNum {
    var i = 0
    fun parse(): SNum {
        if (s[i] == '[') {
            i++
            val l = parse()
            check(s[i++] == ',')
            val r = parse()
            check(s[i++] == ']')
            return SNum.Pair(l, r)
        }
        val start = i
        while (s[i] in '0'..'9') i++
        return SNum.Reg(s.substring(start, i).toInt())
    }
    return parse().also { check(i == s.length) }
}

fun SNum.findPair(n: Int): SNum.Pair? {
    if (n == 0) return this as? SNum.Pair?
    if (this is SNum.Pair) {
        l.findPair(n - 1)?.let { return it }
        r.findPair(n - 1)?.let { return it }
    }
    return null
}

fun SNum.findReg(lim: Int): SNum.Reg? = when(this) {
    is SNum.Reg -> if (x >= lim) this else null
    is SNum.Pair -> {
        l.findReg(lim)?.let { return it }
        r.findReg(lim)?.let { return it }
        null
    }
}

fun SNum.traverse(keep: SNum.Pair): List<SNum> = when(this) {
    is SNum.Reg -> listOf(this)
    is SNum.Pair -> if (this == keep) listOf(this) else l.traverse(keep) + r.traverse(keep)
}

fun SNum.replace(op: Map<SNum, SNum>): SNum {
    op[this]?.let { return it }
    return when(this) {
        is SNum.Reg -> this
        is SNum.Pair -> SNum.Pair(l.replace(op), r.replace(op))
    }
}

fun SNum.reduceOp(): Map<SNum, SNum>? {
    val n = findPair(4)
    if (n != null) {
        check(n.l is SNum.Reg)
        check(n.r is SNum.Reg)
        val op = mutableMapOf<SNum, SNum>(n to SNum.Reg(0))
        val f = traverse(n)
        val i = f.indexOf(n)
        (f.getOrNull(i - 1) as? SNum.Reg)?.let { op[it] = SNum.Reg(it.x + n.l.x) }
        (f.getOrNull(i + 1) as? SNum.Reg)?.let { op[it] = SNum.Reg(it.x + n.r.x) }
        return op
    }
    val r = findReg(10)
    if (r != null) return mapOf(r to SNum.Pair(SNum.Reg(r.x / 2), SNum.Reg((r.x + 1) / 2)))
    return null
}

fun add(a: SNum, b: SNum): SNum =
    generateSequence<SNum>(SNum.Pair(a, b)) { s -> s.reduceOp()?.let { s.replace(it) } }.last()

fun SNum.magnitude(): Int = when(this) {
    is SNum.Reg -> x
    is SNum.Pair -> 3 * l.magnitude() + 2 * r.magnitude()
}

fun main() {
    val dayId = "18"
    val input = readInput("Day${dayId}")
    val a = input.map(::parseSNum)
    val part1 = a.reduce(::add).magnitude()
    println("part1 = $part1")
    val part2 = buildList {
        for (i in a.indices) for (j in a.indices) if (i != j) add(add(a[i], a[j]).magnitude())
    }.maxOrNull()!!
    println("part2 = $part2")
}
