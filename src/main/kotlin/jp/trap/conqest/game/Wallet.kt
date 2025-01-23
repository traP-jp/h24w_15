package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

object Wallet {
    fun setupScoreboard(player: Player) {
        val manager = Bukkit.getScoreboardManager()
        val scoreboard = manager.newScoreboard

        val objective = scoreboard.registerNewObjective("wallet", "dummy", ChatColor.AQUA.toString() + "wallet")
        objective.displaySlot = DisplaySlot.SIDEBAR

        val coin = objective.getScore(ChatColor.YELLOW.toString() + "Coin")
        coin.score = 0

        player.scoreboard = scoreboard
    }

    fun pay(player: Player, use: Int) {
        val scoreboard = player.scoreboard
        val objective = scoreboard.getObjective("wallet") ?: return
        val score = objective.getScore(ChatColor.YELLOW.toString() + "Coin")
        val currentCoin = score.score
        if (currentCoin < use) {
            player.sendMessage("コインが足りません")
            return
        }
        score.score = currentCoin - use
    }

    fun earn(player: Player, get: Int) {
        val scoreboard = player.scoreboard
        val objective = scoreboard.getObjective("wallet") ?: return
        val score = objective.getScore(ChatColor.YELLOW.toString() + "Coin")
        val currentCoin = score.score
        score.score = currentCoin + get
    }
}
