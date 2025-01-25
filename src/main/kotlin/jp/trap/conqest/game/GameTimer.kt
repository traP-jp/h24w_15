package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import java.util.logging.Logger

class GameTimer(private val plugin: Plugin, private val id: String) {
    private var bossBar: BossBar? = null
    private var remainingTime: Int = 0
    private var countDownTask: BukkitRunnable? = null
    private var isPaused: Boolean = false
    private var gameTime: Int = 300
    private val players = mutableSetOf<UUID>()

    private fun createTimer() {
        bossBar = Bukkit.createBossBar(
            ChatColor.YELLOW.toString() + "残り時間" + remainingTime + "秒",
            BarColor.WHITE,
            BarStyle.SOLID
        )
    }

    fun addPlayer(player: Player) {
        players.add(player.uniqueId)
        bossBar?.addPlayer(player)
    }

    fun removePlayer(player: Player) {
        players.remove(player.uniqueId)
        bossBar?.removePlayer(player)
    }

    fun reAddPlayer(player: Player) {
        if(players.contains(player.uniqueId)) {
            bossBar?.addPlayer(player)
        }
    }

    fun startTimer() {
        createTimer()
        countDownTask?.cancel()
        remainingTime = gameTime
        isPaused = false
        bossBar?.setTitle(ChatColor.YELLOW.toString() + "残り時間" + remainingTime + "秒")
        bossBar?.progress = 1.0
        runTimer()
    }

    fun pauseTimer() {
        if (isPaused || countDownTask == null) {
            return
        }
        countDownTask?.cancel()
        countDownTask = null
        isPaused = true
        bossBar?.setTitle(ChatColor.RED.toString() + "ゲーム停止中:" + remainingTime + "秒")
    }

    fun restartTimer() {
        if (!isPaused || remainingTime <= 0) {
            return
        }
        isPaused = false
        bossBar?.setTitle(ChatColor.YELLOW.toString() + "残り時間" + remainingTime + "秒")
        runTimer()
    }

    fun stopTimer() {
        countDownTask?.cancel()
        countDownTask = null
        bossBar?.setTitle(ChatColor.RED.toString() + "ゲーム停止")
        bossBar?.progress = 1.0
        isPaused = false
    }

    fun removeTimer() {
        bossBar?.removeAll()
        bossBar = null
    }

    private fun runTimer() {
        countDownTask = object : BukkitRunnable() {
            override fun run() {
                if (remainingTime <= 0) {
                    bossBar?.setTitle(ChatColor.RED.toString() + "ゲーム終了!!")
                    bossBar?.progress = 0.0
                    this.cancel()
                    return
                }
                bossBar?.setTitle(ChatColor.YELLOW.toString() + "残り時間" + remainingTime + "秒")
                bossBar?.progress = remainingTime.toDouble() / gameTime.toDouble()
                remainingTime--
            }
        }
        countDownTask?.runTaskTimer(plugin, 0L, 20L)
    }
}
