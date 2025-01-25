package jp.trap.conqest.game

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class GameTimerManager(private val plugin: Plugin) {
    private val timers = mutableMapOf<String, GameTimer>()

    fun createAndStartTimer(id: String): GameTimer {
        if (timers.containsKey(id)) {
            throw IllegalArgumentException("ID '$id' のタイマーは既に存在します。")
        }
        val timer = GameTimer(plugin, id)
        timer.startTimer()
        timers[id] = timer
        return timer
    }

    fun addPlayer(id: String, player: Player) {
        timers[id]?.addPlayer(player)
    }

    fun removePlayer(id: String, player: Player) {
        timers[id]?.removePlayer(player)
    }

    fun pauseTimer(id: String) {
        timers[id]?.pauseTimer()
    }

    fun restartTimer(id: String) {
        timers[id]?.restartTimer()
    }

    fun stopTimer(id: String) {
        timers[id]?.stopTimer()
    }

    fun removeTimer(id: String) {
        timers[id]?.removeTimer()
        timers.remove(id)
    }

    fun removeAllTimer() {
        timers.values.forEach { it.removeTimer() }
        timers.clear()
    }

    fun getTimer(id: String): GameTimer? {
        return timers[id]
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        timers.values.forEach { timer ->
            timer.addPlayer(event.player)
        }
    }
}