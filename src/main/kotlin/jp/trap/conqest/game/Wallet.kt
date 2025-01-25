package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import java.util.UUID

object Wallet : Listener {
    private val OBJECTIVE_NAME = ChatColor.YELLOW.toString() + "Coin"
    private val scoreboardMap = mutableMapOf<UUID, Int>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId
        if (scoreboardMap.containsKey(uuid)) {
            setupScoreboard(player, scoreboardMap[uuid]!!)
            scoreboardMap.remove(uuid)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        scoreboardMap[event.player.uniqueId] = event.player.scoreboard.getObjective("wallet")?.getScore(OBJECTIVE_NAME)!!.score
    }

    fun setupScoreboard(player: Player, startCoin: Int) {
        val manager = Bukkit.getScoreboardManager()
        val scoreboard = manager.newScoreboard

        val objective = scoreboard.registerNewObjective("wallet", "dummy", ChatColor.AQUA.toString() + "wallet")
        objective.displaySlot = DisplaySlot.SIDEBAR

        val coin = objective.getScore(OBJECTIVE_NAME)
        coin.score = startCoin

        player.scoreboard = scoreboard
    }

    fun removeScoreboard(player: Player) {
        val scoreboard = player.scoreboard
        scoreboard.getObjective("wallet")?.unregister()
        scoreboardMap.remove(player.uniqueId)
    }

    fun pay(player: Player, use: Int): Boolean {
        val scoreboard = player.scoreboard
        val objective = scoreboard.getObjective("wallet") ?: return false
        val score = objective.getScore(OBJECTIVE_NAME)
        val currentCoin = score.score
        if (currentCoin < use) {
            player.sendMessage("コインが足りません")
            return false
        }
        score.score = currentCoin - use
        return true
    }

    fun earn(player: Player, get: Int) {
        val scoreboard = player.scoreboard
        val objective = scoreboard.getObjective("wallet") ?: return
        val score = objective.getScore(OBJECTIVE_NAME)
        val currentCoin = score.score
        score.score = currentCoin + get
    }
}
