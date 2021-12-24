// NOT FINISHED LOGICAL SIMPLIFICATION

const val MODULO = 26

fun main() {
    val insts = parseDay24().optimized()
    val v0 = SVal(0)
    var inp = 0
    val s = insts.runningFold(SS(v0, v0, v0, v0)) { prev, inst ->
        when (inst) {
            is Inp -> prev.set(inst.r, SInp(++inp))
            is Op -> when (inst.op) {
                OpType.MOV -> prev.set(inst.r, prev[inst.b])
                else -> {
                    val up = SOp(inst.op, prev[inst.r], prev[inst.b])
                    if (up.op in listOf(OpType.DIV, OpType.MOD)) check(up.b is SVal && up.b.v == MODULO)
                    prev.set(inst.r, up)
                }
            }
        }
    }
    val r = s.last().z.simplify().equalsToZero()
    val found = HashSet<SExpr>()
    val t = Temps()
    r.visit { e ->
        if (e in found) {
            if (e !in t.m) {
                t.m[e] = t.a.size
                t.a += e
            }
            false
        } else {
            found += e
            true
        }
    }
    for ((i, e) in t.a.withIndex()) {
        println("tmp$i = ${e.format(t, true)} // ${e.r}")
    }
    println("result = ${r.format(t)}")
}

data class SRange(val first: Int, val last: Int, val modMultiple: Boolean = false) {
    override fun toString(): String = "$first..$last${if (modMultiple) " [M]" else ""}"
}
val BoolRange = SRange(0, 1)
val InpRange = SRange(1, 9, false)

fun SRange.intersects(that: SRange): Boolean {
    val min = maxOf(first, that.first)
    val max = minOf(last, that.last)
    return min <= max
}

fun SRange.union(that: SRange): SRange {
    val min = minOf(first, that.first)
    val max = maxOf(last, that.last)
    return SRange(min, max, modMultiple && that.modMultiple)
}

operator fun SRange.plus(x: Int) = SRange(first + x, last + x)
operator fun SRange.plus(x: SRange) = SRange(first + x.first, last + x.last)
operator fun SRange.times(x: Int) = rangeOf(first * x, last * x)

sealed class SExpr { abstract val r: SRange }
class SVal(val v: Int) : SExpr() { override val r = SRange(v, v, v % MODULO == 0) }
class SInp(val i: Int) : SExpr() { override val r = InpRange }
class SOp(val op: OpType, val a: SExpr, val b: SExpr) : SExpr() {
    override lateinit var r: SRange
    constructor(op: OpType, a: SExpr, b: SExpr, r: SRange) : this(op, a, b) {
        this.r = r
    }
}

// Represents a * (c * (MODULO-1) + 1) + b * c
class SModInd(val c: SExpr, val a: SExpr, val b: SExpr) : SExpr() {
    init { require(c.r == BoolRange) }
    override val r = a.r.union(a.r * MODULO + b.r)
}

class SIf(val c: SExpr, val a: SExpr, val b: SExpr) : SExpr() {
    init { require(c.r == BoolRange) }
    override val r = a.r.union(b.r)
}

class Temps {
    val a = ArrayList<SExpr>()
    val m = HashMap<SExpr, Int>()
}

enum class Lvl { MUL, ADD, CMP, AND, EXPR }

fun String.par(cur: Lvl, lvl: Lvl): String =
    if (cur.ordinal > lvl.ordinal) "($this)" else this

fun SExpr.format(t: Temps, expand: Boolean = false, lvl: Lvl = Lvl.EXPR): String {
    if (!expand) t.m[this]?.let { i -> return "tmp$i" }
    return when (this) {
        is SVal -> v.toString()
        is SInp -> "inp$i"
        is SOp -> {
            val cur = when (op) {
                OpType.ADD -> Lvl.ADD
                OpType.MUL -> Lvl.MUL
                OpType.DIV -> Lvl.MUL
                OpType.MOD -> Lvl.MUL
                OpType.EQL -> Lvl.CMP
                OpType.NEQ -> Lvl.CMP
                OpType.AND -> Lvl.AND
                else -> error("$op")
            }
            "${a.format(t, lvl = cur)} $op ${b.format(t, lvl = cur)}".par(cur, lvl)
        }
        is SModInd -> "modInd(${c.format(t, lvl = Lvl.EXPR)}, ${a.format(t, lvl = Lvl.EXPR)}, ${b.format(t, lvl = Lvl.EXPR)})"
        is SIf -> "if(${c.format(t, lvl = Lvl.EXPR)}, ${a.format(t, lvl = Lvl.EXPR)}, ${b.format(t, lvl = Lvl.EXPR)})"
    }
}

fun SExpr.visit(block: (SExpr) -> Boolean) {
    when (this) {
        is SOp -> {
            if (!block(this)) return
            a.visit(block)
            b.visit(block)
        }
        is SModInd -> {
            if (!block(this)) return
            c.visit(block)
            a.visit(block)
            b.visit(block)
        }
        is SIf -> {
            if (!block(this)) return
            c.visit(block)
            a.visit(block)
            b.visit(block)
        }
        else -> { /* skip */ }
    }
}

fun SExpr.simplify(map: HashMap<SOp, SExpr> = HashMap()): SExpr {
    return when (this) {
        is SVal -> this
        is SInp -> this
        is SOp -> {
            map[this]?.let { return it }
            val a1 = a.simplify(map)
            val b1 = b.simplify(map)
            val res = when {
                a1 is SVal && b1 is SVal -> when (op) {
                    OpType.ADD -> SVal(a1.v + b1.v)
                    OpType.MUL -> SVal(a1.v * b1.v)
                    OpType.DIV -> SVal(a1.v / b1.v)
                    OpType.MOD -> SVal(a1.v % b1.v)
                    OpType.EQL -> SVal(if (a1.v == b1.v) 1 else 0)
                    OpType.NEQ -> SVal(if (a1.v != b1.v) 1 else 0)
                    else -> error("op=$op")
                }
                op == OpType.MUL && (a1 is SVal && a1.v == 0 || b1 is SVal && b1.v == 0) -> SVal(0)
                op == OpType.MUL && a1 is SVal && a1.v == 1 -> b1
                op == OpType.MUL && b1 is SVal && b1.v == 1 -> a1
                op == OpType.ADD && a1 is SVal && a1.v == 0 -> b1
                op == OpType.ADD && b1 is SVal && b1.v == 0 -> a1
                op == OpType.NEQ && !a1.r.intersects(b1.r) -> SVal(1)
                op == OpType.MOD &&
                        a1 is SOp && a1.op == OpType.ADD && a1.b.r.last < MODULO &&
                        a1.a is SOp && a1.a.op == OpType.MUL && a1.a.b.r.modMultiple -> a1.b
                op == OpType.DIV &&
                        a1 is SOp && a1.op == OpType.ADD && a1.b.r.last < MODULO &&
                        a1.a is SOp && a1.a.op == OpType.MUL && a1.a.b.r.modMultiple -> a1.a.a
                op == OpType.ADD && b1 is SVal &&
                        a1 is SOp && a1.op == OpType.ADD && a1.b is SVal ->
                    SOp(OpType.ADD, a1.a, SVal(b1.v + a1.b.v), a1.a.r + (b1.v + a1.b.v))
                op == OpType.ADD && b1 is SOp && b1.op == OpType.MUL && b1.b.r == BoolRange &&
                        a1 is SOp && a1.op == OpType.MUL && a1.b.isModInd(b1.b) && b1.a.r.last < MODULO ->
                    simplifyModInt(b1.b, a1.a, b1.a)
                op == OpType.MOD && a1 is SModInd -> simplifyIf(a1.c, a1.b, a1.a)
                op == OpType.DIV && a1 is SModInd -> simplifyIf(a1.c, a1.a, a1.b)
                op == OpType.ADD && a1 is SIf && b1 is SVal -> simplifyIf(a1.c, a1.a + b1.v, a1.b + b1.v)
                op == OpType.NEQ && a1 is SIf && !a1.b.r.intersects(b1.r) ->
                    SOp(OpType.AND, a1.c, SOp(OpType.NEQ, a1.a, b1, BoolRange), BoolRange)
                else -> {
                    val r = when (op) {
                        OpType.ADD -> a1.r + b1.r
                        OpType.MUL -> rangeOf(
                            a1.r.first * b1.r.first,
                            a1.r.last * b1.r.last,
                            a1.r.first * b1.r.last,
                            a1.r.last * b1.r.first
                        )
                        OpType.DIV -> rangeOf(
                            a1.r.first / b1.r.first,
                            a1.r.last / b1.r.last
                        ).also { require(b1.r.first == b1.r.last) }
                        OpType.MOD -> SRange(0, b1.r.last - 1).also { require(b1.r.first == b1.r.last) }
                        OpType.EQL -> BoolRange
                        OpType.NEQ -> BoolRange
                        else -> error("op=$op")
                    }
                    when {
                        op in listOf(OpType.MUL, OpType.ADD) && a1 is SVal -> SOp(op, b1, a1, r)
                        else -> SOp(op, a1, b1, r)
                    }
                }
            }
            map[this] = res
            res
        }
        else -> error(this::class)
    }
}

// Represents a * (c * (MODULO-1) + 1) + b * c
// c == 1 -> a * MODULO + b
// c == 0 -> a
fun simplifyModInt(c: SExpr, a: SExpr, b: SExpr): SExpr = when {
    c is SOp && c.op == OpType.AND && a is SIf && a.c == c.a -> simplifyIf(c.a, simplifyModInt(c.b, a.a, b), a)
//    c is SIf && a is SIf && c.c == a.c -> simplifyIf(c.c, simplifyModInt(c.a, a.a, b), simplifyModInt(c.b, a.b, b))
    else -> SModInd(c, a, b)
}

fun simplifyIf(c: SExpr, a: SExpr, b: SExpr): SExpr = when {
    b is SIf && b.c == c -> simplifyIf(c, a, b.b)
    else -> SIf(c, a, b)
}

fun SExpr.equalsToZero(): SExpr = SOp(OpType.EQL, this, SVal(0), BoolRange)

operator fun SExpr.plus(v: Int): SExpr = when {
    this is SOp && op == OpType.ADD && b is SVal -> {
        val x = v + b.v
        if (x == 0) a else SOp(OpType.ADD, a, SVal(x), r + x)
    }
    else -> SOp(OpType.ADD, this, SVal(v), r + v)
}

fun SExpr.isModInd(tmp: SExpr): Boolean =
    this is SOp && op == OpType.ADD &&
        a is SOp && a.op == OpType.MUL && a.a == tmp && a.b is SVal && a.b.v == MODULO - 1 &&
        b is SVal && b.v == 1

fun rangeOf(vararg x: Int) = SRange(x.minOrNull()!!, x.maxOrNull()!!)

data class SS(val w: SExpr, val x: SExpr, val y: SExpr, val z: SExpr) {
    operator fun get(r: Int): SExpr = when (r) {
        Regs.W -> w
        Regs.X -> x
        Regs.Y -> y
        Regs.Z -> z
        else -> error("r=$r")
    }
    operator fun get(p: Param): SExpr = when (p) {
        is Reg -> get(p.r)
        is Val -> SVal(p.v)
    }
    fun set(r: Int,v: SExpr): SS = when (r) {
        Regs.W -> copy(w = v)
        Regs.X -> copy(x = v)
        Regs.Y -> copy(y = v)
        Regs.Z -> copy(z = v)
        else -> error("r=$r")
    }
}