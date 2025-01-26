package jp.trap.conqest.game

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.util.Vector
import java.awt.image.BufferedImage
import kotlin.math.atan2

class GameMapRenderer(private val game: Game) : MapRenderer(true) {
    // TODO GameManagerから読み込む
    private val fieldCenter: Pair<Double, Double> = game.field.center.x to game.field.center.z
    private val fieldSize: Pair<Double, Double> = game.field.size.first.toDouble() to game.field.size.second.toDouble()
    private val mapSize: Pair<Int, Int> = Pair(256, 256)

    private val bgImage: BufferedImage = BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB)
    private val inBorder: List<List<Boolean>> = List(bgImage.height) { y ->
        List(bgImage.width) { x ->
            val district = game.field.getDistrict(
                x * game.field.size.first / bgImage.width to y * game.field.size.second / bgImage.height
            )
            listOf(0 to 1, 1 to 1, 1 to 0, 1 to -1, 0 to -1, -1 to -1, -1 to 0, -1 to 1).any { (dx, dy) ->
                val neighbor = game.field.getDistrict(
                    (x + dx) * game.field.size.first / bgImage.width to (y + dy) * game.field.size.second / bgImage.height
                )
                neighbor !== null && neighbor != district
            }
        }
    }
    private val inRoad: List<List<Boolean>> = List(bgImage.height) { y ->
        List(bgImage.width) cell@{ x ->
            val (locX, locZ) = x * game.field.size.first / bgImage.width to y * game.field.size.second / bgImage.height
            val districtIndex = game.field.getDistrict(locX to locZ)?.id ?: return@cell false
            game.field.graph[districtIndex].ifEmpty { setOf(districtIndex) }.minOf { neighbor ->
                val coreLoc = game.field.districts[districtIndex].coreLocation
                val neighborLoc = game.field.districts[neighbor].coreLocation
                // TODO: 道との距離を正しく計算する
                //calcDistanceToLineSegment(
                //    locX.toDouble() to locZ.toDouble(), coreLoc.x to coreLoc.z, neighborLoc.x to neighborLoc.z
                //)
                Double.POSITIVE_INFINITY
            } <= 8
        }
    }

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        for (x in 0 until bgImage.width) for (y in 0 until bgImage.height) {
            val team = if (inBorder[y][x]) null else game.field.getDistrict(
                x * game.field.size.first / bgImage.width to y * game.field.size.second / bgImage.height
            )?.getTeam()
            bgImage.setRGB(x, y, (team?.color?.getRGB() ?: 0xFFFFFF).let {
                var r = it shr 16 and 0xFF
                var g = it shr 8 and 0xFF
                var b = it and 0xFF
                if (inRoad[y][x]) {
                    r = (r * 0.5).toInt()
                    g = (g * 0.5).toInt()
                    b = (b * 0.5).toInt()
                }
                (r shl 16) or (g shl 8) or b
            })
        }
        canvas.drawImage(0, 0, bgImage)
        while (canvas.cursors.size() > 0) canvas.cursors.removeCursor(canvas.cursors.getCursor(0))

        drawCursor(player.location, canvas)
        game.getNites().forEach { nite ->
            drawCursor(nite.getLocation(), canvas)
        }
    }

    private fun drawCursor(location: Location, canvas: MapCanvas) {
        val uvLocation = getUVLocation(location.x, location.z)
        canvas.cursors.addCursor(uvLocation.first - 128, uvLocation.second - 128, getDirection(location))
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