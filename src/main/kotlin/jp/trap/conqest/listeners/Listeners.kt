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
        registerListener(ListenerUsableItem())
        registerListener(ListenerShopGUI())
        registerListener(ListenerNiteControl(plugin.gameManager))
        registerListener(ListenerCustomMap(plugin.gameManager))
        return Result.success(Unit)
    }
}