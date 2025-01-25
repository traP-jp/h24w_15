package jp.trap.conqest.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import kotlin.math.min
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
    private var coreBreakable: Boolean = true
    private val maxHp: Int = 100
    private var hp: Int = maxHp
    private val hpRegenRate: Int = 1
    val savingNite: Nite<*>? = null // TODO

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

    private fun nearCore(location: Location): Boolean {
        val nearDistance = 3.0
        return coreLocation.distance(location) <= nearDistance
    }

    fun updateHp(game: Game) {
        val nearEnemyNites = game.getNites().filter { nearCore(it.getLocation()) && it.getTeam() != team }
        if (nearEnemyNites.isEmpty()) {
            hp += hpRegenRate
        } else {
            val arrackRate: Double = nearEnemyNites.sumOf { nite -> nite.blockBreakSpeed }
            hp -= arrackRate.roundToInt()
        }
        hp = min(maxHp, hp)
    }
}