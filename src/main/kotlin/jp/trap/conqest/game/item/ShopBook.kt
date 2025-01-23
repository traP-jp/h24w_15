package jp.trap.conqest.game.item

import jp.trap.conqest.game.Wallet
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
        val player: Player = event.player
        player.openInventory(inventory)
    }
    private val itemCosts: Map<ItemStack, Int> = mapOf(
        ChanceCard.createChanceCard() to 100,
        ItemStack(Material.IRON_SWORD) to 1000,
        ItemStack(Material.WOODEN_SWORD) to 1,
    )
    val inventory: Inventory = run {
        val inventory = Bukkit.createInventory(null, 27, Component.text("Shop", TextColor.color(0x00AA00)))
        itemCosts.forEach({ (itemStack, cost) ->
            itemStack.lore(listOf(Component.text("Cost: $cost").color(TextColor.color(0x00AA00))))
            inventory.addItem(itemStack)
        })
        inventory
    }
    val onInventoryClicked = { player: Player, item: ItemStack ->
        itemCosts.filter { (itemStack, _) -> itemStack.isSimilar(item) }
            .forEach { (_, amount) -> Wallet.pay(player, amount) }
    }
}