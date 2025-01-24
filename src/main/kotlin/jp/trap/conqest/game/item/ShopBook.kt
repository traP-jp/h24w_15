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

object ShopBook : UsableItem {

    override val item: ItemStack = run {
        val chanceCard = ItemStack(Material.BOOK)
        val meta = chanceCard.itemMeta
        if (meta != null) {
            meta.displayName(Component.text("Open Shop").color(TextColor.color(0x00AA00)))
            chanceCard.setItemMeta(meta)
        }
        chanceCard
    }

    override val onUsed = { event: PlayerInteractEvent ->
        val player: Player = event.player
        player.openInventory(inventory)
    }

    class SellingItem(val item: ItemStack, val cost: Int) {
        val shopItem: ItemStack = item.clone()

        init {
            shopItem.lore(listOf(Component.text("Cost: $cost").color(TextColor.color(0x00AA00))))
        }
    }

    private val sellingItems: List<SellingItem> = listOf(
        SellingItem(ChanceCard.item, 100),
        SellingItem(ItemStack(Material.IRON_SWORD), 1000),
        SellingItem(ItemStack(Material.WOODEN_SWORD), 1),
    )

    val inventory: Inventory = run {
        val inventory = Bukkit.createInventory(null, 27, Component.text("Shop", TextColor.color(0x00AA00)))
        sellingItems.forEach({ sellingItem ->
            inventory.addItem(sellingItem.shopItem)
        })
        inventory
    }

    val onInventoryClicked = { player: Player, item: ItemStack ->
        sellingItems.filter { sellingItem -> sellingItem.shopItem.isSimilar(item) }
            .forEach { sellingItem ->
                if (Wallet.pay(player, sellingItem.cost)) {
                    player.inventory.addItem(sellingItem.item)
                }
            }
    }
}
