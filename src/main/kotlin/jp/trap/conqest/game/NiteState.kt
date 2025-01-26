package jp.trap.conqest.game

import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

enum class NiteStates {
    FOLLOW_MASTER, STATION_DISTRICT, GUARD_DISTRICT, ATTACK, ATTACK_CORE, EARN_COIN, DEAD,
}

sealed class NiteState(val plugin: Plugin, val nite: Nite<*>) {

    abstract val type: NiteStates

    open fun update() {}

    abstract fun exit()

    class FollowMaster(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.FOLLOW_MASTER) :
        NiteState(plugin, nite) {
        private val nearDistance = 5

        override fun update() {
            if (nite.distance(nite.master) >= nearDistance) nite.moveTo(nite.master.location)
            else nite.moveStop()
        }

        override fun exit() {}
    }

    class StationDistrict(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.STATION_DISTRICT) :
        NiteState(plugin, nite) {

        override fun update() {
            // TODO 領土内を歩き回る
        }

        override fun exit() {}
    }

    class GuardDistrict(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.GUARD_DISTRICT) :
        NiteState(plugin, nite) {
        override fun update() {
            // TODO 敵が領土内にいる場合、敵を攻撃 / いなくなったらStationDistrictへ
        }

        override fun exit() {}
    }

    class Attack(
        plugin: Plugin,
        nite: Nite<*>,
        private val target: LivingEntity,
        override val type: NiteStates = NiteStates.ATTACK
    ) : NiteState(plugin, nite) {
        override fun update() {
            if (target.isDead) {
                nite.state = FollowMaster(plugin, nite)
            }
            nite.moveTo(target.location)
            nite.tryAttack(target)
        }

        override fun exit() {}
    }

    class AttackCore(
        plugin: Plugin, nite: Nite<*>, private val target: Block, override val type: NiteStates = NiteStates.ATTACK_CORE
    ) : NiteState(plugin, nite) {
        override fun update() {
            if (target.isEmpty) {
                nite.state = FollowMaster(plugin, nite)
            }
            nite.moveTo(target.location)
            // TODO ブロック破壊
        }

        override fun exit() {}
    }

    class EarnCoin(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.EARN_COIN) :
        NiteState(plugin, nite) {
        private val respawnDelay: Long = 30
        private val task: BukkitTask

        init {
            nite.setVisible(false)
            nite.master.sendMessage(nite.name + "はコイン収集を開始しました")
            task = plugin.server.scheduler.runTaskLater(plugin, Runnable {
                nite.setVisible(true)
                nite.teleport(nite.master.location)
                nite.master.sendMessage(nite.name + "が復活しました")
                Wallet.earn(nite.master, 20) // TODO: パラメータ化
                nite.state = FollowMaster(plugin, nite)
            }, respawnDelay * 20)
        }

        override fun exit() {
            task.cancel()
        }
    }

    class Dead(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.DEAD) :
        NiteState(plugin, nite) {
        private val respawnDelay: Long = 30
        private val task: BukkitTask

        init {
            nite.setVisible(false)
            nite.setAi(false)
            task = plugin.server.scheduler.runTaskLater(plugin, Runnable {
                nite.setAi(true)
                nite.setVisible(true)
                nite.teleport(nite.master.location)
                nite.master.sendMessage(nite.name + "が復活しました")
                nite.state = FollowMaster(plugin, nite)
            }, respawnDelay * 20)
        }

        override fun exit() {
            task.cancel()
        }
    }

}
