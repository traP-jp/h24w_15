package jp.trap.conqest.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class chanceCard implements Listener {

    @EventHandler
    public void onPlayerUseChanceCard(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item != null && item.getType() == Material.CARROT_ON_A_STICK && item.hasItemMeta()){
            ItemMeta meta = item.getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "チャンスカード")){
                event.getPlayer().sendMessage(ChatColor.GREEN + "チャンスカードを使用しました!");
            }
        }
    }

    public static ItemStack createChanceCard(){
        ItemStack chanceCard = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta meta = chanceCard.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.GOLD + "チャンスカード");
            chanceCard.setItemMeta(meta);
        }
        return chanceCard;
    }
}
