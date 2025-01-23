package jp.trap.conqest.game

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ChanceCard {
    companion object {
        fun createChanceCard(): ItemStack {
            val chanceCard = ItemStack(Material.POISONOUS_POTATO)
            val meta = chanceCard.itemMeta
            if (meta != null) {
                meta.setDisplayName("${ChatColor.GOLD}チャンスカード")
                chanceCard.setItemMeta(meta)
            }
            return chanceCard
        }
    }
}
