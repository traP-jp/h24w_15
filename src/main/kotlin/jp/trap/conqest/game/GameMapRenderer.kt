package jp.trap.conqest.game

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.util.Vector
import java.awt.image.BufferedImage
import kotlin.math.atan2

class GameMapRenderer(private val gameManager: GameManager) : MapRenderer(true) {
    // TODO GameManagerから読み込む
    private val fieldCenter: Pair<Double, Double> = Pair(50.0, -100.0)
    private val fieldSize: Pair<Double, Double> = Pair(100.0, 100.0)
    private val mapSize: Pair<Int, Int> = Pair(1024, 1024)

    private val bgImage: BufferedImage

    init {
        bgImage = BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB)
        for (x in 0..10) for (y in 0..20) bgImage.setRGB(x, y, 0x0000FF)
    }

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        canvas.drawImage(0, 0, bgImage)
        while (canvas.cursors.size() > 0) canvas.cursors.removeCursor(canvas.cursors.getCursor(0))

        drawCursor(player.location, canvas)
        gameManager.nites.forEach { nite ->
            drawCursor(nite.getLocation(), canvas)
        }
    }

    private fun drawCursor(location: Location, canvas: MapCanvas) {
        val uvLocation = getUVLocation(location.x, location.z)
        canvas.cursors.addCursor(uvLocation.first, uvLocation.second, getDirection(location))
    }

    private fun getUVLocation(x: Double, y: Double): Pair<Int, Int> {
        val xMin = fieldCenter.first - fieldSize.first / 2
        val yMin = fieldCenter.second - fieldSize.second / 2
        val dx = x - xMin
        val dy = y - yMin
        val xRate = dx / fieldSize.first
        val yRate = dy / fieldSize.second
        return Pair((mapSize.first * xRate).toInt(), (mapSize.second * yRate).toInt())
    }

    private fun getDirection(location: Location): Byte {
        val xzDirection = location.direction.multiply(Vector(-1.0, 0.0, 1.0)).normalize()
        val theta = atan2(xzDirection.x, xzDirection.z) + 2 * Math.PI
        return ((theta + 2 * Math.PI) / (2 * Math.PI) % 1 * 16).toInt().toByte()
    }
}