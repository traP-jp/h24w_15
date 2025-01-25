package jp.trap.conqest.listeners

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import jp.trap.conqest.game.GameManager
import jp.trap.conqest.game.Nite
import jp.trap.conqest.game.NiteState
import jp.trap.conqest.game.NiteStates
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class ListenerNiteControl(private val gameManager: GameManager) : Listener {
    private fun trySetTarget(nite: Nite<*>, target: LivingEntity) {
        // プレイヤーには攻撃しない
        if (target is Player) return
        // 仲間のNiteには攻撃しない TODO: Player単位ではなくTeam単位で制限?
        if (gameManager.getGame(nite.master)?.getNites(nite.master)
                ?.any { friendNite -> friendNite.getUniqueId() == target.uniqueId } == true
        ) return
        // 現在、攻撃可能な状態かどうかを調べる
        if (nite.state.type != NiteStates.FOLLOW_MASTER && nite.state.type != NiteStates.ATTACK) return
        nite.state = NiteState.Attack(gameManager.plugin, nite, target)
    }

    private fun onClickEntity(player: Player, target: Entity) {
        if (target !is LivingEntity) return
        gameManager.getGame(player)?.getNites(player)?.forEach { nite -> trySetTarget(nite, target) }
    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEntityEvent) {
        onClickEntity(event.player, event.rightClicked)
    }

    @EventHandler
    fun onLeftClick(event: PrePlayerAttackEntityEvent) {
        onClickEntity(event.player, event.attacked)
    }

    @EventHandler
    fun onNiteDamagedByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager !is LivingEntity) return
        gameManager.getGames().forEach { game ->
            game.getPlayers().flatMap { player -> gameManager.getGame(player)?.getNites(player) ?: emptyList() }
                .singleOrNull { nite -> nite.getUniqueId() == event.entity.uniqueId }
                ?.let { nite -> trySetTarget(nite, event.damager as LivingEntity) }
        }
    }
}