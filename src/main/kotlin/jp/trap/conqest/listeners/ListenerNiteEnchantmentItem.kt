package jp.trap.conqest.listeners

import jp.trap.conqest.Main
import jp.trap.conqest.game.GameManager
import jp.trap.conqest.game.Nite
import jp.trap.conqest.game.item.NiteEnchantmentItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

class ListenerNiteEnchantmentItem(plugin: Main) : Listener {
    private val gameManager = plugin.gameManager
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

        if (itemInHand.isSimilar(NiteEnchantmentItem.item)) {
            val clickedEntity: Entity = event.rightClicked
            val nite = gameManager.getGames().flatMap { game -> game.getNites() }.firstOrNull { nite -> nite.getUniqueId() == clickedEntity.uniqueId }
            if (nite != null) {
                itemInHand.amount--
                cooldowns[player.uniqueId] = currentTime
                player.sendMessage("${ChatColor.LIGHT_PURPLE}ナイトを強化しました!")
            }
        }
        return
    }
}