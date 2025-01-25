package jp.trap.conqest.game.item

import jp.trap.conqest.game.Nite
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack

object NiteEnchantmentItem : UsableItem{
    override val item: ItemStack = run{
        val niteEnchantmentItem = ItemStack(Material.AMETHYST_SHARD)
        val meta = niteEnchantmentItem.itemMeta
        if(meta != null){
            meta.setDisplayName("${ChatColor.AQUA}ナイト強化アイテム")
            niteEnchantmentItem.setItemMeta(meta)
        }
        niteEnchantmentItem
    }
    override val onUsed = { event: PlayerInteractEntityEvent->
        val player: Player = event.player
        val clickedEntity: Entity = event.rightClicked
        val itemInHand: ItemStack = player.inventory.itemInMainHand
        if(true/*clickedEntity == Nite*/){
            itemInHand.amount--
            player.sendMessage("ナイトを強化しました")
        }
    }
}