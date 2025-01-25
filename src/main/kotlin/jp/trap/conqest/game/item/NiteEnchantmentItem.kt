package jp.trap.conqest.game.item

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object NiteEnchantmentItem {
    val item: ItemStack = run {
        val niteEnchantmentItem = ItemStack(Material.AMETHYST_SHARD)
        val meta = niteEnchantmentItem.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.AQUA}ナイト強化アイテム")
            niteEnchantmentItem.setItemMeta(meta)
        }
        niteEnchantmentItem
    }

}