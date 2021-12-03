fun main() {
    val dayId = "03"
    val input = readInput("Day${dayId}")
    val n = input[0].length
    fun find(ogr: Boolean): Int {
        var rem = input
        var i = 0
        while (i < n && rem.size > 1) {
            var a0 = 0
            var a1 = 0
            for (line in rem) {
                when(line[i]) {
                    '0' -> a0++
                    '1' -> a1++
                }
            }
            val keep = if (ogr) {
                if (a1 >= a0) '1' else '0'
            } else {
                if (a0 <= a1) '0' else '1'
            }
            rem = rem.filter { line -> line[i] == keep }
            i++
        }
        return rem.first().toInt(2)
    }
    println(find(true) * find(false))
}
