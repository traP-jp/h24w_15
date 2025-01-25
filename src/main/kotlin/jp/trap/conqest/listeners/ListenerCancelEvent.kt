package jp.trap.conqest.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerDropItemEvent

class ListenerCancelEvent : Listener {
    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        if (event.hitBlock != null) event.isCancelled = true
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        event.blockList().clear()
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }
}