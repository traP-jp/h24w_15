package jp.trap.conqest.game.item

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object ShopItemNite {
    val itemNormalNite: ItemStack = run {
        val shopItemNormalNite = ItemStack(Material.MUSIC_DISC_13)
        val meta = shopItemNormalNite.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}ノーマルナイト")
            shopItemNormalNite.setItemMeta(meta)
        }
        shopItemNormalNite
    }
    val itemWolfNite: ItemStack = run {
        val shopItemWolfNite = ItemStack(Material.MUSIC_DISC_CAT)
        val meta = shopItemWolfNite.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}オオカミナイト")
            shopItemWolfNite.setItemMeta(meta)
        }
        shopItemWolfNite
    }
    val itemHorseNite: ItemStack = run {
        val shopItemHorseNite = ItemStack(Material.MUSIC_DISC_BLOCKS)
        val meta = shopItemHorseNite.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}ウマナイト")
            shopItemHorseNite.setItemMeta(meta)
        }
        shopItemHorseNite
    }
    val itemIronGolemNite: ItemStack = run {
        val shopItemIronGolemNite = ItemStack(Material.MUSIC_DISC_CHIRP)
        val meta = shopItemIronGolemNite.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}アイアンゴーレムナイト")
            shopItemIronGolemNite.setItemMeta(meta)
        }
        shopItemIronGolemNite
    }
    val itemTurtleNite: ItemStack = run {
        val shopItemTurtleNite = ItemStack(Material.MUSIC_DISC_FAR)
        val meta = shopItemTurtleNite.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}カメナイト")
            shopItemTurtleNite.setItemMeta(meta)
        }
        shopItemTurtleNite
    }
    val itemPhantomNite: ItemStack = run {
        val shopItemPhantomNite = ItemStack(Material.MUSIC_DISC_MALL)
        val meta = shopItemPhantomNite.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}ファントムナイト")
            shopItemPhantomNite.setItemMeta(meta)
        }
        shopItemPhantomNite
    }
    val itemSnowGolemNite: ItemStack = run {
        val shopItemSnowGolemNite = ItemStack(Material.MUSIC_DISC_MELLOHI)
        val meta = shopItemSnowGolemNite.itemMeta
        if (meta != null) {
            meta.setDisplayName("${ChatColor.LIGHT_PURPLE}スノーゴーレムナイト")
            shopItemSnowGolemNite.setItemMeta(meta)
        }
        shopItemSnowGolemNite
    }
}