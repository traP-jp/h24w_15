package jp.trap.conqest.game

import jp.trap.conqest.Main
import jp.trap.conqest.util.Partition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import java.util.*

class Field(
    val plugin: Main, val creatorId: UUID, val center: Location, val partition: Partition, val roadWidth: Double = 3.0
) {

    object Materials {
        val wall = Material.BEDROCK
        val core = Material.BEACON
        val coreBase = Material.IRON_BLOCK
        val fence = Material.STONE_BRICK_WALL
        val road = Material.DIRT_PATH
    }

    private val blockChanges = mutableMapOf<Location, BlockData>()

    init {
        require(roadWidth > 0.0) { "roadWidth must be positive" }

        partition.printRoadWidth = roadWidth

        Bukkit.getPlayer(creatorId)?.let { showPreview(it) }
    }

    fun bottom(): Int = center.blockX - partition.fieldSize.first / 2

    fun top(): Int = bottom() + partition.fieldSize.first

    fun left(): Int = center.blockZ - partition.fieldSize.second / 2

    fun right(): Int = left() + partition.fieldSize.second

    fun showPreview(target: Player) {
        hidePreview()

        // wall
        forEachGrounds(target) { position, ground ->
            if (partition.getDistrictIndex(position) != null) return@forEachGrounds
            ground.y = target.world.minHeight.toDouble()
            while (ground.y <= target.world.maxHeight) {
                blockChanges[ground.clone()] = Materials.wall.createBlockData()
                ground.y += 1.0
            }
        }

        // remove tree
        forEachGrounds(target) { _, ground ->
            while (ground.blockY - 1 >= target.world.minHeight) {
                val material = getChangedBlockData(ground).material
                if (checkIsTree(material)) {
                    blockChanges[ground.clone()] = Material.AIR.createBlockData()
                } else if (material.isCollidable) break
                ground.y -= 1.0
            }
        }

        // cover liquid with glass
        forEachGrounds(target) { _, ground ->
            when (getChangedBlockData(ground).material) {
                Material.WATER -> blockChanges[ground] = Material.LIGHT_BLUE_STAINED_GLASS.createBlockData()
                Material.LAVA -> blockChanges[ground] = Material.ORANGE_STAINED_GLASS.createBlockData()
                else -> {}
            }
        }

        // road
        forEachGrounds(target) { position, ground ->
            if (!partition.inRoad(position, roadWidth)) return@forEachGrounds
            blockChanges[ground] = Materials.road.createBlockData()
        }

        // fence
        forEachGrounds(target) { position, ground ->
            if (partition.getBorderLevel(position) < 1) return@forEachGrounds
            if (partition.inRoad(position, roadWidth)) return@forEachGrounds
            for (x in -2..2) for (z in -2..2) {
                val pos = position.first + x to position.second + z
                if (partition.getBorderLevel(pos) < 1) continue
                if (partition.inRoad(pos, roadWidth)) continue
                val loc = ground.clone().add(x.toDouble(), 1.0, z.toDouble())
                while (loc.blockY >= target.world.minHeight) {
                    if (!getChangedBlockData(loc).material.isCollidable) {
                        blockChanges[loc.clone()] = Materials.fence.createBlockData()
                    }
                    loc.y -= 1.0
                }
            }
        }

        // core
        forEachGrounds(target) { position, ground ->
            if (!partition.isCenter(position)) return@forEachGrounds
            val coreLoc = ground.add(0.0, 1.0, 0.0)
            for (x in -1..1) for (z in -1..1) {
                blockChanges[coreLoc.clone().add(x.toDouble(), -1.0, z.toDouble())] =
                    Materials.coreBase.createBlockData()
            }
            blockChanges[coreLoc] = Materials.core.createBlockData()
        }

        target.sendMultiBlockChange(blockChanges)
        with(target) {
            sendMessage(Component.text("-".repeat(40), NamedTextColor.GREEN))
            sendMessage(Component.text("Previewing at $center"))
            sendMessage(Component.text("  X: ${bottom()} to ${top()}"))
            sendMessage(Component.text("  Z: ${left()} to ${right()}"))
            sendMessage(Component.text("-".repeat(40), NamedTextColor.GREEN))
        }
    }

    fun hidePreview() {
        blockChanges.keys.forEach { it.block.state.update(false, false) }
        blockChanges.clear()
    }

    fun generate() {
        blockChanges.forEach { (loc, data) ->
            loc.block.blockData = data
            if (data.material == Materials.core) {
                // TODO: this.cores[partition.getDistrictIndex(loc.blockX to loc.blockZ)] にコアの座標を設定
            }
        }

        with(plugin.logger) {
            info("-".repeat(40))
            info("Field generated by $creatorId")
            info("  X: ${bottom()} to ${top()}")
            info("  Z: ${left()} to ${right()}")
            partition.toString().split("\n").forEach { info(it) }
            info("-".repeat(40))
        }
    }

    private fun checkIsTree(material: Material): Boolean {
        return Tag.LOGS.isTagged(material) || Tag.LEAVES.isTagged(material) || material == Material.BEE_NEST
    }

    private fun getChangedBlockData(loc: Location): BlockData = blockChanges[loc.clone()] ?: loc.block.blockData.clone()

    private fun forEachGrounds(
        target: Player, action: (position: Pair<Int, Int>, ground: Location) -> Unit
    ) {
        for (i in -1 until partition.fieldSize.first + 1) {
            for (j in -1 until partition.fieldSize.second + 1) {
                val ground = target.world.getHighestBlockAt(bottom() + i, left() + j).location
                while (ground.blockY - 1 >= target.world.minHeight) {
                    val material = getChangedBlockData(ground).material
                    if (material.isCollidable && material != Materials.fence) break
                    // TODO: 水に浸かっているいる水でないブロックに対応
                    if (material == Material.WATER || material == Material.LAVA) break
                    ground.add(0.0, -1.0, 0.0)
                }
                action(i to j, ground)
            }
        }
    }

}
