package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Snowman
import org.bukkit.plugin.Plugin

class SnowGolemNite(location: Location, override val name: String = "一式自走砲", master: Player, plugin: Plugin) :
    Nite<Snowman>(location, EntityType.SNOW_GOLEM, name, master, plugin) {
    override val speed = 0.5
    override val damage = 4.0
    override val handLength = 7.0
    override val attackSpeed = 1.0
    override val health: Double = 30.0

    init {
        super.setEntity(damage, attackSpeed, health)
    }
}