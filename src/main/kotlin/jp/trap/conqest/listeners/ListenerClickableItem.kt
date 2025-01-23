package jp.trap.conqest.listeners

import jp.trap.conqest.game.item.ShopBook
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ListenerClickableItem : Listener {
    companion object {
        val registry: MutableMap<ItemStack, (event: PlayerInteractEvent) -> Any?> =
            mutableMapOf(ShopBook.itemStack to ShopBook.onBookClicked)
    }

    @EventHandler
    fun playerUseChanceCard(event: PlayerInteractEvent) {
        val item = event.item
        val action = event.action

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        registry.keys.filter { itemStack -> item?.isSimilar(itemStack) ?: false }
            .forEach { itemStack -> registry[itemStack]!!(event) }
    }
}