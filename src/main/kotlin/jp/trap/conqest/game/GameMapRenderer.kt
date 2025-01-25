package jp.trap.conqest.game

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.util.Vector
import java.awt.image.BufferedImage
import kotlin.math.atan2
import kotlin.random.Random

class GameMapRenderer(private val game: Game) : MapRenderer(true) {
    // TODO GameManagerから読み込む
    private val fieldCenter: Pair<Double, Double> = game.field.center.x to game.field.center.z
    private val fieldSize: Pair<Double, Double> = game.field.size.first.toDouble() to game.field.size.second.toDouble()
    private val mapSize: Pair<Int, Int> = Pair(1024, 1024)

    private val bgImage: BufferedImage

    init {
        val width = 128
        val height = 128
        bgImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until width) for (y in 0 until height) bgImage.setRGB(
            x, y, game.field.getDistrict(
            x * game.field.size.first / width to y * game.field.size.second / height
        )?.id?.let {
            Random(it).nextInt()
        } ?: 0xFFFFFF

        )
    }

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        canvas.drawImage(0, 0, bgImage)
        while (canvas.cursors.size() > 0) canvas.cursors.removeCursor(canvas.cursors.getCursor(0))

        drawCursor(player.location, canvas)
        game.getNites().forEach { nite ->
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