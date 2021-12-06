fun main() {
    val dayId = "06"
    val input = readInput("Day${dayId}")
    var f = input[0].split(",").map { it.toInt() }
    repeat(80) {
        val g = ArrayList<Int>()
        for (x in f) {
            when {
                x > 0 -> g.add(x - 1)
                x == 0 -> {
                    g.add(6)
                    g.add(8)
                }
            }
        }
        f = g
    }
    println(f.size)
}
