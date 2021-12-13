fun main() {
    val dayId = "13"
    val input = readInput("Day${dayId}")
    data class Pt(val x: Int, val y: Int)
    var a = input.takeWhile { it.isNotEmpty() }.map { it.split(",").map { it.toInt() }.let { (x, y) -> Pt(x, y) }}.toSet()
    val i = input.indexOf("") + 1
    val s = input[i]
    val pr = "fold along "
    check(s.startsWith(pr))
    val ss = s.substring(pr.length)
    val z = ss.substringAfter('=').toInt()
    when (ss[0]) {
        'x' -> {
            a = a.map { p ->
                if (p.x > z) {
                    Pt(2 * z - p.x, p.y)
                } else p
            }.toSet()
        }
        'y' -> {
            a = a.map { p ->
                if (p.y > z) {
                    Pt(p.x, 2 * z - p.y)
                } else p
            }.toSet()
        }
        else -> error(ss)
    }
    println(a.size)
}
