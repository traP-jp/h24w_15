package jp.trap.conqest.listeners

import jp.trap.conqest.game.GameManager
import jp.trap.conqest.game.GameMapRenderer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.MapInitializeEvent
import org.bukkit.map.MapView

class ListenerCustomMap(private val gameManager: GameManager) : Listener {
    @EventHandler
    fun onMapInitialized(event: MapInitializeEvent) {
        Bukkit.broadcast(Component.text("aaa"))
        val view = event.map
        view.scale = MapView.Scale.FARTHEST
        view.centerX = 512
        view.centerZ = 512
        view.isUnlimitedTracking = true
        for (renderer in view.renderers)
            view.removeRenderer(renderer)
        view.addRenderer(GameMapRenderer(gameManager))
    }
}