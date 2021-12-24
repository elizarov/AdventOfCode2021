import kotlin.time.*

fun main() {
    // Parse input
    val insts0 = parseDay24()
    // Optimize program
    val insts = insts0.optimized()
    println("Optimized ${insts0.size} instructions down to ${insts.size}")
    // Find input instructions and their powers
    val n = insts.size
    val pow = LongArray(n)
    var curPow = 1L
    for (i in n - 1 downTo 0) if (insts[i] is Inp) {
        pow[i] = curPow
        curPow *= 10
    }
    // Do simple DFA to find used registers
    val um = IntArray(n + 1)
    um[n] = 1 shl Regs.Z
    for (i in n - 1 downTo 0) {
        val m = um[i + 1]
        val inst = insts[i]
        val rm = 1 shl inst.r
        um[i] = when (inst) {
            is Inp -> m and rm.inv()
            is Op -> if (m and rm == 0) m else when (inst.op) {
                OpType.MOV -> (m and rm.inv()) or inst.b.mask()
                else -> m or inst.b.mask()
            }
        }
    }
    // Solve
    class Task(val part: Int) {
        val dp = Array(n) { HashMap<St, Long>() }
        fun find(i: Int, st: St): Long {
            if (i >= n) return if (st.z == 0) 0L else -1L
            dp[i][st]?.let { return it }
            val ans = when (val inst = insts[i]) {
                is Inp -> {
                    var res = -1L
                    for (v0 in 1..9) {
                        val v = if (part == 1) 10 - v0 else v0
                        val r = find(i + 1, st.set(inst.r, v, um[i + 1]))
                        if (r >= 0) {
                            res = v * pow[i] + r
                            break
                        }
                    }
                    res
                }
                is Op -> when (inst.op) {
                    OpType.ADD -> find(i + 1, st.set(inst.r, st[inst.r] + st[inst.b], um[i + 1]))
                    OpType.MUL -> find(i + 1, st.set(inst.r, st[inst.r] * st[inst.b], um[i + 1]))
                    OpType.DIV -> {
                        val bv = st[inst.b]
                        if (bv == 0) -1L else find(i + 1, st.set(inst.r, st[inst.r] / bv, um[i + 1]))
                    }
                    OpType.MOD -> {
                        val av = st[inst.r]
                        val bv = st[inst.b]
                        if (av < 0 || bv <= 0) -1L else find(i + 1, st.set(inst.r, av % bv, um[i + 1]))
                    }
                    OpType.EQL -> find(i + 1, st.set(inst.r, if (st[inst.r] == st[inst.b]) 1 else 0, um[i + 1]))
                    OpType.NEQ -> find(i + 1, st.set(inst.r, if (st[inst.r] == st[inst.b]) 0 else 1, um[i + 1]))
                    OpType.MOV -> find(i + 1, st.set(inst.r, st[inst.b], um[i + 1]))
                    else -> error("${inst.op}")
                }
            }
            dp[i][st] = ans
            return ans
        }
        fun run() {
            val start = TimeSource.Monotonic.markNow()
            val ans = find(0, St(0, 0, 0, 0))
            println("part$part = $ans   // scanned ${dp.sumOf { it.size }} states in ${start.elapsedNow()}")
        }
    }
    Task(1).run()
    Task(2).run()
}

fun parseDay24(): List<Inst> {
    val dayId = "24"
    return readInput("Day${dayId}").map {
        val s = it.split(" ")
        when (s[0]) {
            "inp" -> Inp(s[1].toReg())
            "add" -> Op(OpType.ADD, s[1].toReg(), s[2].toParam())
            "mul" -> Op(OpType.MUL, s[1].toReg(), s[2].toParam())
            "div" -> Op(OpType.DIV, s[1].toReg(), s[2].toParam())
            "mod" -> Op(OpType.MOD, s[1].toReg(), s[2].toParam())
            "eql" -> Op(OpType.EQL, s[1].toReg(), s[2].toParam())
            else -> error("op=${s[0]}")
        }
    }
}

fun List<Inst>.optimized() = buildList {
    val insts0 = this@optimized
    var i = 0
    while (i < insts0.size) {
        val inst = insts0[i++]
        val next = insts0.getOrNull(i)
        when {
            inst is Op && inst.op == OpType.DIV && inst.b is Val && inst.b.v == 1 -> { /* skip */ }
            inst is Op && inst.op == OpType.MUL && inst.b is Val && inst.b.v == 0 && next is Op &&
                next.op == OpType.ADD && next.r == inst.r -> { i++; add(Op(OpType.MOV, inst.r, next.b)) }
            inst is Op && inst.op == OpType.EQL && next is Op && next.op == OpType.EQL && next.r == inst.r &&
                next.b is Val && next.b.v == 0 -> { i++; add(Op(OpType.NEQ, inst.r, inst.b)) }
            else -> add(inst)
        }
    }
}

object Regs {
    const val W = 0
    const val X = 1
    const val Y = 2
    const val Z = 3
}

fun String.toParam(): Param = when (this) {
    "w" -> Reg(Regs.W)
    "x" -> Reg(Regs.X)
    "y" -> Reg(Regs.Y)
    "z" -> Reg(Regs.Z)
    else -> Val(toInt())
}

fun String.toReg(): Int = (toParam() as Reg).r

fun Param.mask(): Int = when (this) {
    is Reg -> 1 shl r
    is Val -> 0
}

data class St(val w: Int, val x: Int, val y: Int, val z: Int) {
    operator fun get(r: Int): Int = when (r) {
        Regs.W -> w
        Regs.X -> x
        Regs.Y -> y
        Regs.Z -> z
        else -> error("r=$r")
    }
    operator fun get(p: Param): Int = when (p) {
        is Reg -> get(p.r)
        is Val -> p.v
    }
    fun set(r: Int, v: Int, mask: Int): St {
        var w1 = w
        var x1 = x
        var y1 = y
        var z1 = z
        when (r) {
            Regs.W -> w1 = v
            Regs.X -> x1 = v
            Regs.Y -> y1 = v
            Regs.Z -> z1 = v
            else -> error("r=$r")
        }
        if ((mask and (1 shl Regs.W)) == 0) w1 = 0
        if ((mask and (1 shl Regs.X)) == 0) x1 = 0
        if ((mask and (1 shl Regs.Y)) == 0) y1 = 0
        if ((mask and (1 shl Regs.Z)) == 0) z1 = 0
        return St(w1, x1, y1, z1)
    }
}

sealed class Param
data class Reg(val r: Int) : Param()
data class Val(val v: Int) : Param()

sealed class Inst { abstract val r: Int }
data class Inp(override val r: Int) : Inst()
data class Op(val op: OpType, override val r: Int, val b: Param) : Inst()

enum class OpType(val str: String) {
    ADD("+"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    EQL("=="),
    NEQ("!="),
    MOV("<-"),
    AND("&&");

    override fun toString(): String = str
}


