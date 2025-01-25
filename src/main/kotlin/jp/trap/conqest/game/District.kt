package jp.trap.conqest.game

import org.bukkit.Location
import org.bukkit.scoreboard.Team

class District(val id: Int, private val locations: Set<Pair<Int, Int>>, private val coreLocation: Location, private var team: Team? = null) {
    fun setTeam(team: Team) {
        this.team = team
    }
    fun contains(location: Pair<Int, Int>): Boolean {
        return location in locations
    }
}