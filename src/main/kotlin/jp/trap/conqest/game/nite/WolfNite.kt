package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.plugin.Plugin

class WolfNite(location: Location, override val name: String = "デューン", master: Player, plugin: Plugin) :
    Nite<Wolf>(location, EntityType.WOLF, name, master, plugin) {
    override val speed = 0.9
    override val damage = 5.0
    override val handLength = 2.0
    override val attackSpeed = 1.0
    override val health: Double = 12.0
    init {
        super.setEntity(damage, attackSpeed, health)
    }
}