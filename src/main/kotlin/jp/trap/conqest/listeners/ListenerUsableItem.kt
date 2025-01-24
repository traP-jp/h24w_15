package jp.trap.conqest.listeners

import jp.trap.conqest.game.item.UsableItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class ListenerUsableItem : Listener {
    companion object {
        val registry: List<UsableItem> = mutableListOf()
    }

    @EventHandler
    fun playerUseChanceCard(event: PlayerInteractEvent) {
        val item = event.item
        val action = event.action

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        registry.filter { usableItem -> usableItem.item.isSimilar(item) }
            .forEach { usableItem -> usableItem.onUsed(event) }
    }
}