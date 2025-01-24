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
        val districts = gameManager.field!!.districts
        val district = districts.firstOrNull { district -> district.core.location == event.block.location }
        val team = gameManager.getTeam(player)
        if (district != null && team != null) {
            event.isCancelled = true
            district.setTeam(team)
        }
    }

    @EventHandler
    fun onBlockBreakProgressChanged(event: BlockBreakProgressUpdateEvent) {
        if (event.entity !is Player) return
        val player: Player = event.entity as Player
        val districts = gameManager.field!!.districts
        districts.forEach { district -> player.sendMessage(district.core.location.toString()) }
        val district = districts.firstOrNull { district -> district.core.location == event.block.location }
        district?.core?.changeHP(100 - event.progress.toDouble() * 100)
    }
}