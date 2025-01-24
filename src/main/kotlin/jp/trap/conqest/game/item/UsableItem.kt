package jp.trap.conqest.game.item

import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

interface UsableItem {
    val item: ItemStack
    val onUsed: (PlayerInteractEvent) -> Any?
}