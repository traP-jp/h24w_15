package jp.trap.conqest.game

import org.bukkit.Location

class District(val gameManager: GameManager, val core: DistrictCore, private var team: Team) {
    init {
        setTeam(team)
    }

    fun isContained(location: Location): Boolean {
        return false
    }

    fun setTeam(team: Team) {
        gameManager.broadcastMessage("team changed to ${team.color}")
        this.team = team
        core.location.block.type = team.color.getConcreteMaterial()
    }
}