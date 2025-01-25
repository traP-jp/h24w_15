package jp.trap.conqest.game

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

abstract class Nite<T>(
    location: Location, type: EntityType, name: String, val master: Player, val plugin: Plugin
) where T : Entity, T : Mob {
    private var entity: T = location.world.spawnEntity(
        location, type, false
    ) as T
    protected open val speed = 0.5
    protected open val damage = 1.0
    protected open val handLength = 3.0
    protected open val attackSpeed = 1.0
    open val blockBreakSpeed: Double = 1.0
    var state: NiteState = NiteState.FollowMaster(plugin, this)
    abstract val name: String

    init {
        entity.customName(Component.text(name))
        entity.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, Int.MAX_VALUE, 1))
        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) == null) {
            entity.registerAttribute(Attribute.ATTACK_DAMAGE)
            entity.getAttribute(Attribute.ATTACK_DAMAGE)?.baseValue = damage
        }
        plugin.server.scheduler.runTaskTimer(plugin, Runnable { state.update() }, 0, 1)
    }


    fun tryAttack(target: Entity): Boolean {
        entity.lookAt(target)
        if (entity.location.distance(target.location) >= handLength) return false
        entity.attack(target)
        return true
    }

    fun moveTo(target: Location) {
        entity.lookAt(target)
        entity.pathfinder.moveTo(target, speed)
    }

    fun moveStop() {
        entity.pathfinder.stopPathfinding()
    }

    fun setVisible(visible: Boolean) {
        entity.isInvisible = visible.not()
        entity.setNoPhysics(visible.not())
        entity.setGravity(visible)
        entity.isCollidable = visible
        entity.health = entity.getAttribute(Attribute.MAX_HEALTH)!!.value
    }

    fun getVisible(): Boolean {
        return entity.isInvisible.not()
    }

    fun teleport(location: Location) {
        entity.teleport(location)
    }

    fun distance(target: Entity): Double {
        return entity.location.distance(target.location)
    }

    fun getLocation(): Location {
        return entity.location
    }

    fun getUniqueId(): UUID {
        return entity.uniqueId
    }

    fun setAi(value: Boolean) {
        entity.setAI(value)
    }

    fun getTeam(): Team {
        TODO()
    }

}