package jp.trap.conqest.game

import jp.trap.conqest.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import java.util.*

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

    fun addGlow(entity: Entity) {
        players.forEach {
            Main.instance.glowingEntities.setGlowing(entity, Bukkit.getPlayer(it), color.getChatColor())
        }
    }
}

enum class TeamColor {
    GRAY, RED, BLUE;

    fun getGlassMaterial(): Material = when (this) {
        GRAY -> Material.GRAY_STAINED_GLASS_PANE
        RED -> Material.RED_STAINED_GLASS_PANE
        BLUE -> Material.BLUE_STAINED_GLASS_PANE
    }

    fun getRGB(): Int = when (this) {
        GRAY -> 0x7D7D73
        RED -> 0x8E2121
        BLUE -> 0x2D2F8F
    }

    fun getChatColor(): ChatColor = when (this) {
        GRAY -> ChatColor.GRAY
        RED -> ChatColor.RED
        BLUE -> ChatColor.BLUE
    }
}