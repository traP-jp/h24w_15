package jp.trap.conqest.listeners

import jp.trap.conqest.game.Wallet
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ListenerWallet : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        Wallet.onPlayerJoin(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent){
        Wallet.onPlayerQuit(event)
    }
}