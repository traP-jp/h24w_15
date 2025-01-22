package jp.trap.conqest.game

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ChanceCard :Listener{
    @EventHandler
    fun PlayerUseChanceCard(event: PlayerInteractEvent) {
        val item = event.item
        if (item != null && item.type == Material.CARROT_ON_A_STICK && item.hasItemMeta()) {
            val meta = item.itemMeta
            if (meta.hasDisplayName() && meta.displayName == ChatColor.GOLD.toString() + "チャンスカード") {
                event.player.sendMessage(ChatColor.GREEN.toString() + "チャンスカードを使用しました!")
            }
        }
    }

    companion object {
        fun createChanceCard(): ItemStack {
            val chanceCard = ItemStack(Material.CARROT_ON_A_STICK)
            val meta = chanceCard.itemMeta
            if (meta != null) {
                meta.setDisplayName(ChatColor.GOLD.toString() + "チャンスカード")
                chanceCard.setItemMeta(meta)
            }
            return chanceCard
        }
    }
}
