package jp.trap.conqest.listeners

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class ListenerPlayerUseChanceCard : Listener{
    @EventHandler
    fun playerUseChanceCard(event : PlayerInteractEvent) {
        val player = event.player
        val item = event.item
        val action = event.action
        
        if (item == null || item.type != Material.POISONOUS_POTATO || !item.hasItemMeta() || (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)) {
            return
        }
        val meta = item.itemMeta
        if (meta != null && meta.hasDisplayName() && meta.displayName == "${ChatColor.GOLD}チャンスカード") {
            player.sendMessage("${ChatColor.GREEN}チャンスカードを使用しました")
        }
    }
}