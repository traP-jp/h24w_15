package jp.trap.conqest.game

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GameManager(val plugin: Plugin) {

    private var state: GameState = GameState.BeforeGame(this)
    private val players: MutableList<Player> = mutableListOf()

    fun setState(state: GameState) {
        this.state = state
    }

    fun addPlayer(player: Player) {
        player.setResourcePack("https://trap-jp.github.io/h24w_15/conqest_texture.zip")
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