package jp.trap.conqest.game

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.inventory.ItemStack

class ChanceCard {
    companion object {
    fun playerUseChanceCard(player: Player, item : ItemStack?, action: Action){
        if(item == null || item.type != Material.POISONOUS_POTATO || !item.hasItemMeta() || (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)){
            return
        }
        val meta = item.itemMeta
        if(meta != null && meta.hasDisplayName() && meta.displayName == "${ChatColor.GOLD}チャンスカード"){
            player.sendMessage("${ChatColor.GREEN}チャンスカードを使用しました")
        }
    }

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
