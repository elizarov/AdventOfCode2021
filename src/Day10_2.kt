fun main() {
    val dayId = "10"
    val input = readInput("Day${dayId}")
    val a = ArrayList<Long>()
    for (ss in input) {
        val s = ArrayList<Char>()
        var ok = true
        for (c in ss) {
            when(c) {
                '(' -> s += ')'
                '[' -> s += ']'
                '{' -> s += '}'
                '<' -> s += '>'
                else -> {
                    if (s.isEmpty()) {
                        ok = false
                        break
                    }
                    val cc = s.removeLast()
                    if (cc != c) {
                        ok = false
                        break
                    }
                }
            }
        }
        if (ok && s.isNotEmpty()) {
            var sc = 0L
            while (s.isNotEmpty()) {
                val c = s.removeLast()
                sc *= 5
                when (c) {
                    ')' -> sc += 1
                    ']' -> sc += 2
                    '}' -> sc += 3
                    '>' -> sc += 4
                }
            }
            a += sc
        }
    }
    a.sort()
    println(a[a.size / 2])
}
