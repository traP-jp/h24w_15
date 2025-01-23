package jp.trap.conqest.listeners

import jp.trap.conqest.Main
import jp.trap.conqest.game.ChanceCard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class Listeners(
    private val plugin: Main
) :Listener{
    private fun registerListener(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin)
    }
    fun init(): Result<Unit> {
        registerListener(this)
        registerListener(ListenerPlayerUseChanceCard())
        return Result.success(Unit)
    }
}