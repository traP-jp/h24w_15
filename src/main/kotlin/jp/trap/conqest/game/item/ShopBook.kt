package jp.trap.conqest.game.item

import jp.trap.conqest.Main
import jp.trap.conqest.game.Wallet
import jp.trap.conqest.game.nite.*
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

    class SellingItem(val item: ItemStack, val cost: Int, val onClick: ((Player) -> Unit)?) {
        val shopItem: ItemStack = item.clone()

        init {
            shopItem.lore(listOf(Component.text("Cost: $cost").color(TextColor.color(0x00AA00))))
        }
    }

    private val sellingItems: List<SellingItem> = listOf(
        SellingItem(ChanceCard.item, 100, null),
        SellingItem(ItemStack(Material.IRON_SWORD), 1000, null),
        SellingItem(ItemStack(Material.WOODEN_SWORD), 1, null),
        SellingItem(ShopItemNite.itemNormalNite, 10) { player ->
            Main.instance.gameManager.getGame(player)
                ?.addNite(NormalNite(player.location, "NormalNite", player, Main.instance), player)
        },
        SellingItem(ShopItemNite.itemWolfNite, 30) { player ->
            Main.instance.gameManager.getGame(player)
                ?.addNite(WolfNite(player.location, "WolfNite", player, Main.instance), player)
        },
        SellingItem(ShopItemNite.itemHorseNite, 30) { player ->
            Main.instance.gameManager.getGame(player)
                ?.addNite(HorseNite(player.location, "HorseNite", player, Main.instance), player)
        },
        SellingItem(ShopItemNite.itemIronGolemNite, 30) { player ->
            Main.instance.gameManager.getGame(player)
                ?.addNite(IronGolemNite(player.location, "IronGolemNite", player, Main.instance), player)
        },
        SellingItem(ShopItemNite.itemTurtleNite, 30) { player ->
            Main.instance.gameManager.getGame(player)
                ?.addNite(TurtleNite(player.location, "TurtleNite", player, Main.instance), player)
        },
        SellingItem(ShopItemNite.itemPhantomNite, 30) { player ->
            Main.instance.gameManager.getGame(player)
                ?.addNite(PhantomNite(player.location, "PhantomNite", player, Main.instance), player)
        },
        SellingItem(ShopItemNite.itemSnowGolemNite, 30) { player ->
            Main.instance.gameManager.getGame(player)
                ?.addNite(SnowGolemNite(player.location, "SnowGolemNite", player, Main.instance), player)
        },
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
                    if (sellingItem.onClick == null) {
                        player.inventory.addItem(sellingItem.item)
                    } else {
                        sellingItem.onClick.invoke(player)
                    }
                }
            }
    }
}
