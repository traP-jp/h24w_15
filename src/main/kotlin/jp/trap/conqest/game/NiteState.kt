package jp.trap.conqest.game

import jp.trap.conqest.Main
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import kotlin.math.roundToInt
import kotlin.random.Random

enum class NiteStates {
    FOLLOW_MASTER, STATION_DISTRICT, GUARD_DISTRICT, ATTACK, ATTACK_CORE, EARN_COIN, DEAD,
}

sealed class NiteState(val plugin: Plugin, val nite: Nite<*>) {

    abstract val type: NiteStates

    open fun update() {}

    class FollowMaster(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.FOLLOW_MASTER) :
        NiteState(plugin, nite) {
        private val nearDistance = 5

        override fun update() {
            if (nite.distance(nite.master) >= nearDistance) nite.moveTo(nite.master.location)
            else nite.moveStop()
        }
    }

    class StationDistrict(
        plugin: Plugin,
        nite: Nite<*>,
        private val district: District,
        private val field: Field,
        override val type: NiteStates = NiteStates.STATION_DISTRICT
    ) : NiteState(plugin, nite) {
        var walkTask: BukkitTask? = null
        var checkTask: BukkitTask? = null

        override fun update() {
            plugin.logger.info("StationDistrict");
            walkTask = plugin.server.scheduler.runTaskTimer(plugin, Runnable {
                var loc = -1 to -1
                while (district.contains(loc).not()) loc =
                    Random.nextInt() % field.size.first to Random.nextInt() % field.size.second

                nite.moveTo(
                    Location(
                        nite.getLocation().world,
                        field.x_min + loc.first.toDouble(),
                        0.0,
                        field.y_min + loc.second.toDouble()
                    )
                )
            }, 0, 20 * 10)
            checkTask = plugin.server.scheduler.runTaskTimer(plugin, Runnable {
                val game = Main.instance.gameManager.getGame(nite.master) ?: return@Runnable
                if (game.getNites().any { enemyNite ->
                        district.contains(enemyNite.getLocation().x.roundToInt() to enemyNite.getLocation().y.roundToInt()) && enemyNite.team != nite.team
                    }) {
                    nite.state = GuardDistrict(plugin, nite, district, field)
                    walkTask?.cancel()
                    checkTask?.cancel()
                }
            }, 0, 1)
        }

        // TODO terminate
    }

    class GuardDistrict(
        plugin: Plugin,
        nite: Nite<*>,
        val district: District,
        private val field: Field,
        override val type: NiteStates = NiteStates.GUARD_DISTRICT
    ) : NiteState(plugin, nite) {
        var checkTask: BukkitTask? = null
        override fun update() {
            checkTask = plugin.server.scheduler.runTaskTimer(plugin, Runnable {
                val game = Main.instance.gameManager.getGame(nite.master) ?: return@Runnable
                val enemyNite = game.getNites().firstOrNull { enemyNite ->
                    district.contains(enemyNite.getLocation().x.roundToInt() to enemyNite.getLocation().y.roundToInt()) && enemyNite.team != nite.team
                } ?: run {
                    nite.state = StationDistrict(plugin, nite, district, field)
                    return@Runnable
                }
                nite.moveTo(enemyNite.getLocation())
                nite.tryAttack(enemyNite.entity)
            }, 0, 1)
            // TODO 敵が領土内にいる場合、敵を攻撃 / いなくなったらStationDistrictへ
        }
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
    }

    class EarnCoin(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.EARN_COIN) :
        NiteState(plugin, nite) {
        private val respawnDelay: Long = 30

        init {
            nite.setVisible(false)
            nite.master.sendMessage(nite.name + "はコイン収集を開始しました")
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                nite.setVisible(true)
                nite.teleport(nite.master.location)
                nite.master.sendMessage(nite.name + "が復活しました")
                Wallet.earn(nite.master, 20) // TODO: パラメータ化
                nite.state = FollowMaster(plugin, nite)
            }, respawnDelay * 20)
        }
    }

    class Dead(plugin: Plugin, nite: Nite<*>, override val type: NiteStates = NiteStates.DEAD) :
        NiteState(plugin, nite) {
        private val respawnDelay: Long = 30

        init {
            nite.setVisible(false)
            nite.setAi(false)
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                nite.setAi(true)
                nite.setVisible(true)
                nite.teleport(nite.master.location)
                nite.master.sendMessage(nite.name + "が復活しました")
                nite.state = FollowMaster(plugin, nite)
            }, respawnDelay * 20)
        }
    }

}
