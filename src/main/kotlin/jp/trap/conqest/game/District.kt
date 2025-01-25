package jp.trap.conqest.game

import org.bukkit.Location
import kotlin.math.min
import kotlin.math.roundToInt

class District(
    val id: Int,
    private val locations: Set<Pair<Int, Int>>,
    private val coreLocation: Location,
    private var team: Team? = null
) {
    val savingNite: Nite<*>? = null // TODO

    private val maxHp: Int = 100
    private var hp: Int = maxHp
    private val hpRegenRate: Int = 1

    fun setTeam(team: Team) {
        this.team = team
    }

    fun contains(location: Pair<Int, Int>): Boolean {
        return location in locations
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