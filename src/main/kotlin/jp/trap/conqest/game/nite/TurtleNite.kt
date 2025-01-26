package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Turtle
import org.bukkit.plugin.Plugin

class TurtleNite(location: Location, override val name: String = "パゴス", master: Player, plugin: Plugin) :
    Nite<Turtle>(location, EntityType.TURTLE, name, master, plugin) {
    override val speed = 0.5
    override val damage = 1.0
    override val handLength = 3.0
    override val attackSpeed = 1.0
    override val health = 50.0
    init {
        super.setEntity(damage, attackSpeed, health)
    }
}