package jp.trap.conqest.game

import org.bukkit.Material
import java.util.UUID


class Team(val color: TeamColor) {
    companion object {
        val emptyTeam: Team = Team(TeamColor.GRAY)
    }

    private val players = mutableListOf<UUID>()
    fun addPlayer(player: UUID) {
        this.players.add(player)
    }

    fun getPlayers(): List<UUID> {
        return players
    }
}

enum class TeamColor {
    GRAY,
    RED,
    BLUE;

    fun getGlassMaterial(): Material {
        return when (this) {
            GRAY -> Material.GRAY_STAINED_GLASS_PANE
            RED -> Material.RED_STAINED_GLASS_PANE
            BLUE -> Material.BLUE_STAINED_GLASS_PANE
        }
    }
}