package jp.trap.conqest.listeners

import jp.trap.conqest.Main
import org.bukkit.Bukkit
import org.bukkit.event.Listener

class Listeners(
    private val plugin: Main
) {
    private fun registerListener(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin)
    }
    fun init(): Result<Unit> {
        return Result.success(Unit)
    }

}