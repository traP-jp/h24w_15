package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Turtle
import org.bukkit.plugin.Plugin

class TurtleNite(location: Location, override val name: String = "パゴス", master: Player, plugin: Plugin) :
    Nite<Turtle>(location, EntityType.TURTLE, name, master, plugin) {
    override val speed = 0.3
    override val damage = 2.0
    override val handLength = 2.0
    override val attackSpeed = 6.0
    override val health = 100.0
    init {
        super.setEntity(damage, attackSpeed, health)
    }
}