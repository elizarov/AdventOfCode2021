fun main() {
    val dayId = "12"
    val input = readInput("Day${dayId}")
    val g = HashMap<String,HashSet<String>>()
    for (s in input) {
        val (a, b) = s.split("-")
        g.getOrPut(a) { HashSet() }.add(b)
        g.getOrPut(b) { HashSet() }.add(a)
    }
    var ans = 0
    val vs = HashSet<String>()
    fun find(a: String, vt: Boolean) {
        if (a == "end") {
            ans++
            return
        }
        for (b in g[a]!!) {
            if (b == "start") continue
            val small = b[0] in 'a'..'z'
            var nvt = vt
            if (small) {
                if (b in vs) {
                    if (vt) continue
                    nvt = true
                } else {
                    vs += b
                }
            }
            find(b, nvt)
            if (small && nvt == vt) vs -= b
        }
    }
    find("start", false)
    println(ans)
}
