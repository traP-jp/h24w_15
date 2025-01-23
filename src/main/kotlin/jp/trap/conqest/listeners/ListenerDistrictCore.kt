package jp.trap.conqest.listeners

import jp.trap.conqest.game.District
import jp.trap.conqest.game.GameManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class ListenerDistrictCore(private val gameManager: GameManager) : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        player.sendMessage(event.eventName)
        val coreLocations = gameManager.field.districts.map { district: District -> district.core.location }
        if (coreLocations.contains(event.block.location)) {
            player.sendMessage("Core Destroyed!")
            event.isCancelled = true
            event.block.type = Material.RED_CONCRETE
        }
    }
}