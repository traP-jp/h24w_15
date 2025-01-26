package jp.trap.conqest.game

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class GameTimerManager(private val plugin: Plugin, private val gameTime: Long, private val id: Int) {
    private val timers = mutableMapOf<Int, GameTimer>()

    fun createAndStartTimer(): GameTimer {
        if (timers.containsKey(id)) {
            throw IllegalArgumentException("ID '$id' のタイマーは既に存在します。")
        }
        val timer = GameTimer(plugin, gameTime)
        timer.startTimer()
        timers[id] = timer
        return timer
    }

    fun addPlayer(player: Player) {
        timers[id]?.addPlayer(player)
    }

    fun removePlayer(player: Player) {
        timers[id]?.removePlayer(player)
    }

    fun pauseTimer() {
        timers[id]?.pauseTimer()
    }

    fun restartTimer() {
        timers[id]?.restartTimer()
    }

    fun stopTimer() {
        timers[id]?.stopTimer()
    }

    fun removeTimer() {
        timers[id]?.removeTimer()
        timers.remove(id)
    }

    fun removeAllTimer() {
        timers.values.forEach { it.removeTimer() }
        timers.clear()
    }

    fun getTimer(): GameTimer? {
        return timers[id]
    }

    fun onPlayerJoin(event: PlayerJoinEvent) {
        timers.values.forEach { timer ->
            timer.addPlayer(event.player)
        }
    }
}