package jp.trap.conqest.listeners

import jp.trap.conqest.game.GameTimerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class ListenerGameTimer(private val plugin: Plugin) : Listener {
    private var gameTimerManager = GameTimerManager(plugin)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        gameTimerManager.onPlayerJoin(event)
    }
}