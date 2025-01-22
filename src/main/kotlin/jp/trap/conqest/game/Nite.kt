package jp.trap.conqest.game

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.plugin.Plugin

abstract class Nite<T>(location: Location, type: EntityType, name: String, plugin: Plugin)
        where T : Entity, T : Mob {
    private var entity: T = location.world.spawnEntity(
        location, type, false
    ) as T
    private var target: LivingEntity? = null
    private var attack: Boolean = true
    private val speed = 0.5
    private val damage = 1.0

    init {
        entity.customName(Component.text(name))
        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) == null) {
            entity.registerAttribute(Attribute.ATTACK_DAMAGE)
            entity.getAttribute(Attribute.ATTACK_DAMAGE)?.baseValue = damage
        }
        plugin.server.scheduler.runTaskTimer(plugin, Runnable { update() }, 0, 1)
    }

    private fun update() {
        if (target != null) {
            entity.pathfinder.moveTo(target!!, speed)
            if (attack && entity.location.distance(target!!.location) <= 3) {
                entity.attack(target!!)
            }
        } else {
            entity.pathfinder.stopPathfinding()
        }
    }

}