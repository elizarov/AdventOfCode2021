fun main() {
    val dayId = "10"
    val input = readInput("Day${dayId}")
    var ans = 0
    for (ss in input) {
        val s = ArrayList<Char>()
        for (c in ss) {
            when(c) {
                '(' -> s += ')'
                '[' -> s += ']'
                '{' -> s += '}'
                '<' -> s += '>'
                else -> {
                    if (s.isEmpty()) break
                    val cc = s.removeLast()
                    if (cc != c) {
                        when(c) {
                            ')' -> ans += 3
                            ']' -> ans += 57
                            '}' -> ans += 1197
                            '>' -> ans += 25137
                            else -> error("!!!")
                        }
                        break
                    }
                }
            }
        }
    }
    println(ans)
}
