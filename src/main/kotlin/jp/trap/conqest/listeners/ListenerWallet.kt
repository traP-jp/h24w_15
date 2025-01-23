package jp.trap.conqest.listeners

import jp.trap.conqest.game.Wallet
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ListenerWallet : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        Wallet.setupScoreboard(player)
    }
}
