package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class HorseNite(location: Location, override val name: String = "ディノール", master: Player, plugin: Plugin) :
    Nite<Horse>(location, EntityType.HORSE, name, master, plugin) {
    override val speed = 0.9
    override val damage = 7.0
    override val handLength = 2.0
    override val attackSpeed = 1.0
    override val health: Double = 80.0
    init {
        super.setEntity(damage, attackSpeed, health)
    }
}