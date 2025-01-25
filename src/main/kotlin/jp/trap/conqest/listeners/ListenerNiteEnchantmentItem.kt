package jp.trap.conqest.listeners

import jp.trap.conqest.game.Nite
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

class ListenerNiteEnchantmentItem : Listener {
    private val cooldowns: MutableMap<UUID, Long> = mutableMapOf()
    private val cooldownTime: Long = 10L

    @EventHandler
    fun onPlayerUse(event: PlayerInteractEntityEvent) {
        val player = event.player
        val itemInHand: ItemStack = player.inventory.itemInMainHand
        if (itemInHand.type == Material.AIR || itemInHand.type != Material.AMETHYST_SHARD) return
        val currentTime = System.currentTimeMillis()
        if (cooldowns.containsKey(player.uniqueId)) {
            val lastUsed = cooldowns[player.uniqueId]!!
            if (currentTime - lastUsed < cooldownTime) {
                return
            }
        }
        val itemMeta = itemInHand.itemMeta
        if (itemMeta != null && itemMeta.hasDisplayName() && itemMeta.displayName == "${ChatColor.AQUA}ナイト強化アイテム") {
            val clickedEntity: Entity = event.rightClicked
            if (Nite.isNiteEntity(clickedEntity)) {
                itemInHand.amount--
                cooldowns[player.uniqueId] = currentTime
                player.sendMessage("${ChatColor.LIGHT_PURPLE}ナイトを強化しました!")
            }
        }
        return
    }
}