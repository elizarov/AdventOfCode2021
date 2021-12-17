fun main() {
    val dayId = "17"
    val input = readInput("Day${dayId}")
    val (xr, yr) = input[0].removePrefix("target area: ").split(", ")
    val (x1, x2) = xr.removePrefix("x=").split("..").map { it.toInt() }
    val (y1, y2) = yr.removePrefix("y=").split("..").map { it.toInt() }
    var ans = 0
    for (vx0 in 1..1000) for (vy0 in -1000..1000) {
        var vx = vx0
        var vy = vy0
        var x = 0
        var y = 0
        var ok = false
        while (x <= x2 && y >= y1) {
            x += vx
            y += vy
            if (x in x1..x2 && y in y1..y2) {
                ok = true
                break
            }
            if (vx > 0) vx--
            vy--
        }
        if (ok) ans++
    }
    println(ans)
}
