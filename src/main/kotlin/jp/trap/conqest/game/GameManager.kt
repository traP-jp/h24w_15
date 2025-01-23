package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GameManager(val plugin: Plugin) {

    private var state: GameState = GameState.BeforeGame(this)
    private val players: MutableList<Player> = mutableListOf()
    val field: GameField
    init {
        // TODO
        field = GameField(Bukkit.getWorlds()[0], listOf(District(DistrictCore(Location(Bukkit.getWorlds()[0], 70.0, 70.0, -110.0)))))
    }

    fun setState(state: GameState) {
        this.state = state
    }

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun broadcastMessage(msg: String) {
        players.forEach {
            it.sendMessage(msg)
        }
    }

    fun executeCommand(command: GameCommand, sender: CommandSender): Int {
        return state.executeCommand(command, sender)
    }

}