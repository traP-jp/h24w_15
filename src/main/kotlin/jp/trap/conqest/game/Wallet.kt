package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

object Wallet {
    private val OBJECTIVE_NAME = ChatColor.YELLOW.toString() + "Coin"
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
