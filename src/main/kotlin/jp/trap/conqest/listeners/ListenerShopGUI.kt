package jp.trap.conqest.listeners

import jp.trap.conqest.game.item.ShopBook
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ListenerShopGUI : Listener {
    @EventHandler
    fun onPlayerClickItem(event: InventoryClickEvent) {
        if (event.clickedInventory == ShopBook.inventory && event.currentItem != null) {
            event.isCancelled = true
            ShopBook.onInventoryClicked(event.currentItem!!)
        }
    }
}