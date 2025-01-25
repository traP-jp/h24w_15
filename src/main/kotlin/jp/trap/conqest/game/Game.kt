package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class Game(val plugin: Plugin, val field: Field) {
    private var state: GameState = GameState.BeforeGame(this)
    private val playersUUID: MutableList<UUID> = mutableListOf()
    private val nites: MutableMap<UUID, MutableList<Nite<*>>> = mutableMapOf()
    lateinit var lobby: Location

    fun setState(state: GameState) {
        this.state = state
    }

    fun getStateType(): GameStates {
        return state.type
    }

    fun addPlayer(player: Player) {
        player.setResourcePack("https://trap-jp.github.io/h24w_15/conqest_texture.zip")
        playersUUID.add(player.uniqueId)
        lobby = player.location // TODO ロビーの場所へ変更
    }

    fun broadcastMessage(msg: String) {
        playersUUID.forEach {
            Bukkit.getPlayer(it)?.sendMessage(msg)
        }
    }

    fun getPlayers(): List<Player> {
        val players: MutableList<Player> = mutableListOf()
        playersUUID.forEach{
            players.add(Bukkit.getPlayer(it)!!)
        }
        return players
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

    fun judge() {
        // TODO Teamを返すようにする
    }

}