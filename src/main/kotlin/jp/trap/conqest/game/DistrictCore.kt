package jp.trap.conqest.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import kotlin.math.roundToInt

class DistrictCore(district: District, val location: Location) {
    private val block: Block = location.block
    val armorStand: ArmorStand
    private var hp: Double = 100.0

    init {
        block.type = district.getTeam().color.getConcreteMaterial()
        armorStand = location.world.spawnEntity(
            location.clone().add(Vector(0.5, -1.0, 0.5)), EntityType.ARMOR_STAND
        ) as ArmorStand
        armorStand.setNoPhysics(true)
        armorStand.setGravity(false)
        armorStand.isVisible = false
        armorStand.isCustomNameVisible = true
        changeHP(50.0)
        changeHP(20.0)
    }

    fun changeHP(hp: Double) {
        this.hp = hp
        armorStand.customName(Component.text(hp.roundToInt().toString() + "%").color(NamedTextColor.WHITE))
    }

    fun onBreak(attackerTeam: Team) {
        block.type = attackerTeam.color.getConcreteMaterial()
    }
}