package jp.trap.conqest.game

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class GameManager(val plugin: Plugin) {

    private var state: GameState = GameState.BeforeGame(this)
    private val players: MutableList<Player> = mutableListOf()
    private val nites: MutableMap<UUID, MutableList<Nite<*>>> = mutableMapOf()

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

    fun getNites(player: Player): List<Nite<*>> {
        return nites.computeIfAbsent(player.uniqueId) { ArrayList() }
    }

    fun addNite(nite: Nite<*>, master: Player) {
        nites.computeIfAbsent(master.uniqueId) { ArrayList() }.add(nite)
    }

}