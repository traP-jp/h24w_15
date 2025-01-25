package jp.trap.conqest.listeners

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import jp.trap.conqest.game.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack

class ListenerNiteControl(private val gameManager: GameManager) : Listener {
    private val controlItem: ItemStack = run {
        val item = ItemStack(Material.STICK)
        val meta = item.itemMeta
        meta.displayName(Component.text("指示棒").color(TextColor.color(0x00FF00)))
        item.itemMeta = meta
        item
    }

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
        val player = event.player
        val target = event.rightClicked
        val nite =
            gameManager.getGame(player)?.getNites(player)?.filter { it.getUniqueId() == target.uniqueId }?.first()
                ?: run {
                    onClickEntity(player, target)
                    return
                }
        event.isCancelled = true
        if (player.inventory.itemInMainHand.isSimilar(controlItem)) {
            // アイテムを持って右クリックしたら、コイン収集
            nite.state = NiteState.EarnCoin(gameManager.plugin, nite)
        } else {
            // 素手で右クリックしたら、選択の切り替え
            nite.toggleSelected()
        }
    }

    @EventHandler
    fun onLeftClick(event: PrePlayerAttackEntityEvent) {
        val player = event.player
        val target = event.attacked
        val nite =
            gameManager.getGame(player)?.getNites(player)?.filter { it.getUniqueId() == target.uniqueId }?.first()
                ?: run {
                    onClickEntity(player, target)
                    return
                }
        event.isCancelled = true
        if (player.inventory.itemInMainHand.isSimilar(controlItem)) {
            // アイテムを持って左クリックしたら、コイン収集
            nite.state = NiteState.EarnCoin(gameManager.plugin, nite)
        } else {
            // 素手で左クリックしたら、自分に追従
            nite.state = NiteState.FollowMaster(gameManager.plugin, nite)
        }
    }

    @EventHandler
    fun onNiteDamagedByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager !is LivingEntity) return
        gameManager.getGames().forEach { game ->
            game.getPlayers().flatMap { player -> gameManager.getGame(player)?.getNites(player) ?: emptyList() }
                .singleOrNull { nite -> nite.getUniqueId() == event.entity.uniqueId }?.let { nite ->
                    if (nite.getVisible()) trySetTarget(nite, event.damager as LivingEntity)
                    else event.isCancelled = true
                }
        }
    }

    @EventHandler
    fun onNiteDeath(event: EntityDeathEvent) {
        val nite = gameManager.getGames().flatMap { game -> game.getNites() }
            .singleOrNull { nite -> nite.getUniqueId() == event.entity.uniqueId }
        val game = nite?.master?.let { gameManager.getGame(it) }
        if (nite == null || game == null) return
        // killコマンドは受け入れる
        if (event.damageSource.damageType == DamageType.GENERIC_KILL) {
            game.removeNite(nite)
            return
        }
        nite.state = NiteState.Dead(gameManager.plugin, nite)
        event.isCancelled = true
    }
}