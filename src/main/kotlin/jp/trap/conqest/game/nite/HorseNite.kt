package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class HorseNite(location: Location, override val name: String = "ウマ", master: Player, plugin: Plugin) :
    Nite<Horse>(location, EntityType.HORSE, name, master, plugin) {
    override val speed = 0.5
    override val damage = 1.0
    override val handLength = 3.0
    override val attackSpeed = 1.0
}