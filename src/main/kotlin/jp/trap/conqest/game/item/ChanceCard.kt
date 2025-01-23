package jp.trap.conqest.game.item

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object ChanceCard : UsableItem {
    override val item: ItemStack = run {
        val chanceCard = ItemStack(Material.POISONOUS_POTATO)
        val meta = chanceCard.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.GOLD}チャンスカード")
            chanceCard.setItemMeta(meta)
        }
        chanceCard
    }
    override val onUsed = { event: PlayerInteractEvent ->
        event.player.sendMessage("${ChatColor.GREEN}チャンスカードを使用しました")
    }
}
