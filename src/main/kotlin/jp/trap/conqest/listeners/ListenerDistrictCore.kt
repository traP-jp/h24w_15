package jp.trap.conqest.listeners

import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent
import jp.trap.conqest.game.GameManager
import jp.trap.conqest.game.NiteState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent

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
            district.setHp(0, team)
        }
    }

    @EventHandler
    fun onBlockBreakProgressChanged(event: BlockBreakProgressUpdateEvent) {
        if (event.entity !is Player) return
        val player: Player = event.entity as Player
        val game = gameManager.getGame(player) ?: return
        val districts = game.field.districts
        val district = districts.firstOrNull { district -> district.coreLocation.block == event.block.location.block }
        district?.setHp((100 - event.progress.toDouble() * 100).toInt(), game.getTeam(player))
    }

    @EventHandler
    fun onCoreClicked(event: PlayerInteractEvent) {
        val player: Player = event.player
        player.sendMessage(event.clickedBlock.toString())
        val game = gameManager.getGame(player) ?: return
        player.sendMessage(event.clickedBlock.toString() + "1")
        val block = event.clickedBlock ?: return
        player.sendMessage(event.clickedBlock.toString() + "2")

        player.sendMessage(event.clickedBlock.toString() + "3")
        val district = game.field.districts.firstOrNull { district -> player.sendMessage(district.coreLocation.block.toString()); district.coreLocation.block.location == block.location } ?: return
        player.sendMessage(event.clickedBlock.toString() + "3")
        val playerTeam = game.getTeam(player)
        if (district.getTeam() == playerTeam) {
            // 自分のチームの場合、守りに行く
            game.getNites(player).filter { it.getSelected() }.forEach { nite ->
                nite.state = NiteState.StationDistrict(game.plugin, nite, district, game.field)
                district.savingNite.add(nite)
            }

        } else {
            // 自分以外のチームの場合、攻撃しに行く
            game.getNites(player).forEach { nite -> nite.state = NiteState.AttackCore(game.plugin, nite, block) }
        }
    }
}