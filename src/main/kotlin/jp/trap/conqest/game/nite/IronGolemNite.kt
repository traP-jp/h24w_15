package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.IronGolem
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class IronGolemNite(
    location: Location,
    override val name: String = "アイアンゴーレム",
    master: Player,
    plugin: Plugin
) :
    Nite<IronGolem>(location, EntityType.IRON_GOLEM, name, master, plugin) {
    override val speed = 0.5
    override val damage = 1.0
    override val handLength = 3.0
    override val attackSpeed = 1.0
}