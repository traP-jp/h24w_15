package jp.trap.conqest.game.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ShopBook {
    val itemStack: ItemStack = run {
        val chanceCard = ItemStack(Material.BOOK)
        val meta = chanceCard.itemMeta
        if (meta != null) {
            meta.displayName(Component.text("Open Shop").color(TextColor.color(0x00AA00)))
            chanceCard.setItemMeta(meta)
        }
        chanceCard
    }
    val onBookClicked = { event: PlayerInteractEvent ->
        var player: Player = event.player
    }
    val inventory: Inventory = run {
        val inventory = Bukkit.createInventory(null, 27, Component.text("Shop", TextColor.color(0x00AA00)))
        inventory.addItem(ChanceCard.createChanceCard())
        inventory
    }
    val onInventoryClicked = { item: ItemStack ->
        if(ChanceCard.createChanceCard().isSimilar(item)) {
            // TODO: 支払い
        }
    }
}