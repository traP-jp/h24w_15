package jp.trap.conqest.game

import jp.trap.conqest.game.gamestate.BeforeGameState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GameManager(val plugin: Plugin) {

    private var state: GameState = BeforeGameState(this)
    private val players: MutableList<Player> = mutableListOf()

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