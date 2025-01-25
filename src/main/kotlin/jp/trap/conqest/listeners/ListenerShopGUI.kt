package jp.trap.conqest.listeners

import jp.trap.conqest.game.item.ShopBook
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class ListenerShopGUI : Listener {
    @EventHandler
    fun onPlayerClickItem(event: InventoryClickEvent) {
        if (event.clickedInventory != ShopBook.inventory) return
        if (event.currentItem == null) return

        event.isCancelled = true

        if (event.click != ClickType.LEFT) return

        ShopBook.onInventoryClicked.invoke(event.whoClicked as Player, event.currentItem!!)
    }
}