package jp.trap.conqest.listeners

import jp.trap.conqest.game.GameManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class ListenerDistrictCore(private val gameManager: GameManager) : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        player.sendMessage(event.eventName)
        val districts = gameManager.field.districts
        val district = districts.firstOrNull { district -> district.coreLocation == event.block.location }
        val team = gameManager.getTeam(player)
        if (district != null && team != null) {
            event.isCancelled = true
            district.setTeam(team)
        }
    }
}