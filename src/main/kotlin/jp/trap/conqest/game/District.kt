package jp.trap.conqest.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import kotlin.math.roundToInt

class District(
    val id: Int,
    private val locations: Set<Pair<Int, Int>>,
    val coreLocation: Location,
    private var team: Team = Team.emptyTeam
) {
    private val coreBlock: Block = coreLocation.block
    private val coreArmorStand: ArmorStand
    private var hp: Double = 100.0
    private var coreBreakable: Boolean = true

    init {
        coreBlock.type = team.color.getConcreteMaterial()
        coreArmorStand = coreLocation.world.spawnEntity(
            coreLocation.clone().add(Vector(0.5, -1.0, 0.5)), EntityType.ARMOR_STAND
        ) as ArmorStand
        coreArmorStand.setNoPhysics(true)
        coreArmorStand.setGravity(false)
        coreArmorStand.isVisible = false
        coreArmorStand.isCustomNameVisible = true
        coreArmorStand.customName(Component.text(""))
        changeHP(hp)
    }

    fun getTeam(): Team {
        return team
    }

    fun setTeam(team: Team) {
        coreBlock.type = team.color.getConcreteMaterial()
        this.team = team
    }

    fun contains(location: Pair<Int, Int>): Boolean {
        return location in locations
    }

    fun changeHP(hp: Double): Boolean {
        if (coreBreakable.not()) return false
        this.hp = hp
        coreArmorStand.customName(Component.text(hp.roundToInt().toString() + "%").color(NamedTextColor.WHITE))
        return true
    }

    fun onBreak(attackerTeam: Team): Boolean {
        if (coreBreakable.not()) return false
        coreBlock.type = attackerTeam.color.getConcreteMaterial()
        return true
    }

    fun setBreakable(breakable: Boolean) {
        this.coreBreakable = breakable
    }
}