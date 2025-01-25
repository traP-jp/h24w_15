package jp.trap.conqest.listeners

import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent
import jp.trap.conqest.game.GameManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class ListenerDistrictCore(private val gameManager: GameManager) : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val game = gameManager.getGame(player) ?: return
        val districts = game.field.districts
        val district = districts.firstOrNull { district -> district.coreLocation.block == event.block.location.block }
        val team = game.getTeam(player)
        if (district != null && team != null) {
            event.isCancelled = true
            district.setTeam(team)
        }
    }

    @EventHandler
    fun onBlockBreakProgressChanged(event: BlockBreakProgressUpdateEvent) {
        if (event.entity !is Player) return
        val player: Player = event.entity as Player
        val game = gameManager.getGame(player) ?: return
        val districts = game.field.districts
        val district = districts.firstOrNull { district -> district.coreLocation.block == event.block.location.block }
        district?.changeHP((100 - event.progress.toDouble() * 100).toInt())
    }
}