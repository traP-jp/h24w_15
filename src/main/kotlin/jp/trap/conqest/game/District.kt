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
        setHp(hp)
    }

    fun getTeam(): Team {
        return team
    }

    private fun setTeam(team: Team) {
        coreBlock.type = team.color.getConcreteMaterial()
        this.team = team
    }

    fun contains(location: Pair<Int, Int>): Boolean {
        return location in locations
    }

    fun setHp(hp: Int, by: Team? = Team.emptyTeam): Boolean {
        if (coreBreakable.not()) return false
        this.hp = hp
        coreArmorStand.customName(Component.text("$hp%").color(NamedTextColor.WHITE))
        if (hp <= 0) {
            setTeam(by ?: Team.emptyTeam)
            setHp(100)
        }
        return true
    }

    fun setBreakable(breakable: Boolean) {
        this.coreBreakable = breakable
    }

    private fun nearCore(location: Location): Boolean {
        val nearDistance = 3.0
        return coreLocation.distance(location) <= nearDistance
    }

    // 毎tick実行される
    fun updateHp(game: Game) {
        val nearEnemyNites = game.getNites().filter { nearCore(it.getLocation()) && it.team != team }
        var hpChange = 0
        hpChange += hpRegenRate
        val arrackRate: Double = nearEnemyNites.sumOf { nite -> nite.blockBreakSpeed }
        hpChange -= arrackRate.roundToInt()
        if (nearEnemyNites.isNotEmpty()) game.broadcastMessage(hpChange.toString())
        val brokenTeam = if (nearEnemyNites.isEmpty()) Team.emptyTeam else nearEnemyNites.random().team
        setHp(min(maxHp, hp + hpChange), brokenTeam)
    }
}