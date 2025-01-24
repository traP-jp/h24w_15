package jp.trap.conqest.game.nite

import jp.trap.conqest.game.Nite
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.plugin.Plugin

class NormalNite(location: Location, override val name: String = "ノーマル", master: Player, plugin: Plugin) :
    Nite<Villager>(location, EntityType.VILLAGER, name, master, plugin)