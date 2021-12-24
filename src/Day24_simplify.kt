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
    println("result = \n  ${r.format(t)}")
}

data class SRange(val p: Int, val q: Int, val modMultiple: Boolean = false) {
    override fun toString(): String = "$p..$q${if (modMultiple) " [M]" else ""}"
}
val BoolRange = SRange(0, 1)
val InpRange = SRange(1, 9, false)

fun SRange.intersects(that: SRange): Boolean {
    val p = maxOf(p, that.p)
    val q = minOf(q, that.q)
    return p <= q
}

fun SRange.union(that: SRange): SRange {
    val p = minOf(p, that.p)
    val q = maxOf(q, that.q)
    return SRange(p, q, modMultiple && that.modMultiple)
}

operator fun SRange.plus(x: Int) = SRange(p + x, q + x)
operator fun SRange.plus(x: SRange) = SRange(p + x.p, q + x.q)
operator fun SRange.times(x: Int) = rangeOf(p * x, q * x)

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
    init { require(c.r == BoolRange && b.r.p >= 0 && b.r.q < MODULO) }
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

enum class Lvl { MUL, ADD, CMP, AND, OR, EXPR }

fun String.par(cur: Lvl, lvl: Lvl): String =
    if (cur.ordinal > lvl.ordinal) "($this)" else this

fun SExpr.format(t: Temps, expand: Boolean = false, lvl: Lvl = Lvl.EXPR, nest: Int = 0): String {
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
                OpType.OR -> Lvl.OR
                else -> error("$op")
            }
            "${a.format(t, lvl = cur, nest = nest)} $op${if (op == OpType.AND && nest == 0) "\n  " else ""} ${b.format(t, lvl = cur, nest = nest)}".par(cur, lvl)
        }
        is SModInd -> "modInd(${c.format(t, lvl = Lvl.EXPR, nest = nest + 1)}; ${a.format(t, lvl = Lvl.EXPR, nest = nest + 1)}; ${b.format(t, lvl = Lvl.EXPR, nest = nest + 1)})"
        is SIf -> "if(${c.format(t, lvl = Lvl.EXPR, nest = nest + 1)}, ${a.format(t, lvl = Lvl.EXPR, nest = nest + 1)}, ${b.format(t, lvl = Lvl.EXPR, nest = nest + 1)})"
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
            val a = a.simplify(map)
            val b = b.simplify(map)
            val res = when {
                a is SVal && b is SVal -> when (op) {
                    OpType.ADD -> SVal(a.v + b.v)
                    OpType.MUL -> SVal(a.v * b.v)
                    OpType.DIV -> SVal(a.v / b.v)
                    OpType.MOD -> SVal(a.v % b.v)
                    OpType.EQL -> SVal(if (a.v == b.v) 1 else 0)
                    OpType.NEQ -> SVal(if (a.v != b.v) 1 else 0)
                    else -> error("op=$op")
                }
                op == OpType.MUL && (a is SVal && a.v == 0 || b is SVal && b.v == 0) -> SVal(0)
                op == OpType.MUL && a is SVal && a.v == 1 -> b
                op == OpType.MUL && b is SVal && b.v == 1 -> a
                op == OpType.ADD && a is SVal && a.v == 0 -> b
                op == OpType.ADD && b is SVal && b.v == 0 -> a
                op == OpType.DIV &&
                    a is SOp && a.op == OpType.ADD && a.b.r.q < MODULO &&
                    a.a is SOp && a.a.op == OpType.MUL && a.a.b.r.modMultiple -> a.a.a
                op == OpType.ADD && b is SVal -> a + b.v
                op == OpType.ADD && b is SOp && b.op == OpType.MUL && b.b.r == BoolRange &&
                    a is SOp && a.op == OpType.MUL && a.b.isModInd(b.b) && b.a.r.q < MODULO ->
                        simplifyModInt(b.b, a.a, b.a)
                op == OpType.MOD -> a.simplifyMod()
                op == OpType.DIV -> a.simplifyDiv()
                op == OpType.NEQ -> simplifyNeq(a, b)
                else -> {
                    val r = when (op) {
                        OpType.ADD -> a.r + b.r
                        OpType.MUL -> rangeOf(a.r.p * b.r.p, a.r.q * b.r.q, a.r.p * b.r.q, a.r.q * b.r.p)
                        OpType.DIV -> rangeOf(a.r.p / b.r.p, a.r.q / b.r.q).also { require(b.r.p == b.r.q) }
                        OpType.MOD -> SRange(0, b.r.q - 1).also { require(b.r.p == b.r.q) }
                        OpType.EQL -> BoolRange
                        OpType.NEQ -> BoolRange
                        else -> error("op=$op")
                    }
                    when {
                        op in listOf(OpType.MUL, OpType.ADD) && a is SVal -> SOp(op, b, a, r)
                        else -> SOp(op, a, b, r)
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
    else -> SModInd(c, a, b)
}

fun simplifyIf(c: SExpr, a: SExpr, b: SExpr): SExpr = when {
    b is SIf && b.c == c -> simplifyIf(c, a, b.b)
    a is SVal && a.v == 0 -> simplifyAnd(c.equalsToZero(), b)
    b is SVal && b.v == 0 -> simplifyAnd(c, a)
    b is SVal && b.v == 1 -> simplifyOr(c.equalsToZero(), a)
    else -> SIf(c, a, b)
}

fun SExpr.simplifyMod(): SExpr = when {
    this is SOp && op == OpType.ADD && b.r.q < MODULO &&
        a is SOp && a.op == OpType.MUL && a.b.r.modMultiple -> b
    this is SModInd -> simplifyIf(c, b, a)
    this is SIf -> simplifyIf(c, a.simplifyMod(), b.simplifyMod())
    else -> SOp(OpType.MOD, this, SVal(MODULO), SRange(0, MODULO - 1))
}

fun SExpr.simplifyDiv(): SExpr = when {
    r.p >= 0 && r.q < MODULO -> SVal(0)
    this is SOp && op == OpType.ADD && a is SOp && a.op == OpType.MUL && b.r.p >= 0 && b.r.q < MODULO -> a.a
    this is SModInd -> simplifyIf(c, a, a.simplifyDiv())
    this is SIf -> simplifyIf(c, a.simplifyDiv(), b.simplifyDiv())
    else -> SOp(OpType.DIV, this, SVal(MODULO), SRange(r.p / MODULO, (r.q + MODULO - 1) / MODULO))
}

fun SExpr.equalsToZero(): SExpr = when {
    r.p > 0 -> SVal(0)
    this is SModInd -> simplifyAnd(a.equalsToZero(), simplifyOr(c.equalsToZero(), b.equalsToZero()))
    this is SOp && op == OpType.NEQ -> simplifyEql(a, b)
    this is SOp && op == OpType.AND -> simplifyOr(a.equalsToZero(), b.equalsToZero())
    this is SOp && op == OpType.OR -> simplifyAnd(a.equalsToZero(), b.equalsToZero())
    this is SOp && op == OpType.EQL && b is SVal && b.v == 0 && a.r.p >= 0 && a.r.p <= 1 -> a
    this is SOp && op == OpType.EQL -> simplifyNeq(a, b)
    this is SIf -> simplifyIf(c, a.equalsToZero(), b.equalsToZero())
    else -> SOp(OpType.EQL, this, SVal(0), BoolRange)
}

fun simplifyEql(a: SExpr, b: SExpr): SExpr = when {
    else -> SOp(OpType.EQL, a, b, BoolRange)
}

fun simplifyNeq(a: SExpr, b: SExpr): SExpr = when {
    !a.r.intersects(b.r) -> SVal(1)
    a is SIf -> simplifyIf(a.c, simplifyNeq(a.a, b), simplifyNeq(a.b, b))
    else -> SOp(OpType.NEQ, a, b, BoolRange)
}

fun simplifyAnd(a: SExpr, b: SExpr) = when {
    b is SVal && b.v == 1 -> a
    a is SVal && a.v == 1 -> b
    b is SVal && b.v == 0 -> SVal(0)
    a is SVal && a.v == 0 -> SVal(0)
    else -> SOp(OpType.AND, a, b, BoolRange)
}

fun simplifyOr(a: SExpr, b: SExpr) = when {
    b is SVal && b.v == 0 -> a
    a is SVal && a.v == 0 -> b
    else -> SOp(OpType.OR, a, b, BoolRange)
}

operator fun SExpr.plus(v: Int): SExpr = when {
    this is SOp && op == OpType.ADD && b is SVal -> {
        val x = v + b.v
        if (x == 0) a else SOp(OpType.ADD, a, SVal(x), a.r + x)
    }
    this is SIf -> simplifyIf(c, a + v, b + v)
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