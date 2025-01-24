package jp.trap.conqest.listeners

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import jp.trap.conqest.game.Game
import jp.trap.conqest.game.NiteState
import jp.trap.conqest.game.NiteStates
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class ListenerNiteControl(private val game: Game) : Listener {
    private fun onClickEntity(player: Player, target: Entity) {
        if (target !is LivingEntity) return
        game.getNites(player)
            .filter { nite -> nite.state.type == NiteStates.FOLLOW_MASTER || nite.state.type == NiteStates.ATTACK }
            .forEach { nite -> nite.state = NiteState.Attack(game.plugin, nite, target) }
    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEntityEvent) {
        onClickEntity(event.player, event.rightClicked)
    }

    @EventHandler
    fun onLeftClick(event: PrePlayerAttackEntityEvent) {
        onClickEntity(event.player, event.attacked)
    }
}