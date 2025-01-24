package jp.trap.conqest.game

import org.bukkit.Location

class District(val gameManager: GameManager, private val coreLocation: Location, private var team: Team) {
    val core: DistrictCore = DistrictCore(this, coreLocation)

    init {
        setTeam(team)
    }

    fun isContained(location: Location): Boolean {
        return false
    }

    fun getTeam(): Team {
        return team
    }

    fun setTeam(team: Team) {
        gameManager.broadcastMessage("team changed to ${team.color}")
        this.team = team
        core.onBreak(team)
    }
}