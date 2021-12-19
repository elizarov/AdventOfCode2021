import java.awt.image.*
import java.io.*
import java.util.*
import javax.imageio.*

// ffmpeg -framerate 50 -i vis/Day15_%06d.png -c:v libx265 Day15vis.mp4
fun main() {
    val emptyColor = 0x000000
    val queuedColor = 0x00ff00
    val visitedMod = 100
    val imageStep = 100
    val visitedColor = IntArray(visitedMod) { i ->
        0xff0000 + 0x0000ff * i / (visitedMod - 1)
    }
    val dir = File("vis")
    dir.mkdirs()

    val dayId = "15"
    val a0 = readInput("Day${dayId}").map { it.toCharArray().map { it.digitToInt() } }
    val n0 = a0.size
    val m0 = a0[0].size
    val n = 5 * n0
    val m = 5 * m0
    val a = Array(n) { i -> IntArray(m) { j ->
        val k = i / n0 + j / m0
        (a0[i % n0][j % m0] + k - 1) % 9 + 1
    } }
    val d = Array(n) { IntArray(m) { Int.MAX_VALUE } }
    data class Pos(val i: Int, val j: Int, val x: Int)
    val v = Array(n) { BooleanArray(m) }
    val q = PriorityQueue(compareBy(Pos::x))
    val image = BufferedImage(m, n, BufferedImage.TYPE_INT_RGB)
    for (j in 0 until m) for (i in 0 until n) image.setRGB(j, i, emptyColor)
    var imageIndex = 1
    fun flushImage() {
        val file = File(dir, "Day15_${imageIndex++.toString().padStart(6, '0')}.png")
        println("Writing $file")
        ImageIO.write(image, "png", file);

    }
    flushImage()
    var stepIndex = 0
    fun enqueue(i: Int, j: Int, x: Int) {
        d[i][j] = x
        q.add(Pos(i, j, x))
        image.setRGB(j, i, queuedColor)
        if (stepIndex++ % imageStep == 0) flushImage()
    }
    fun relax(i: Int, j: Int, x: Int) {
        if (i !in 0 until n || j !in 0 until m || v[i][j]) return
        val xx = x + a[i][j]
        if (xx < d[i][j]) enqueue(i, j, xx)
    }
    enqueue(0, 0, 0)
    while (!v[n - 1][m - 1]) {
        val (i, j, x) = q.remove()
        if (v[i][j]) continue
        image.setRGB(j, i, visitedColor[x % visitedMod])
        v[i][j] = true
        relax(i - 1, j, x)
        relax(i + 1, j, x)
        relax(i, j - 1, x)
        relax(i, j + 1, x)
    }
    flushImage()
    val ans = d[n - 1][m - 1]
    println(ans)
}
