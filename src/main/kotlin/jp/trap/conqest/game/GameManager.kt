package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GameManager(val plugin: Plugin) {

    private var state: GameState = GameState.BeforeGame(this)

    private val teams: MutableList<Team> = mutableListOf(Team(TeamColor.GRAY))

    // TODO TwoSquirrelsのライブラリを使うように置き換える
    var field: GameField? = null

    fun setState(state: GameState) {
        this.state = state
    }

    fun addPlayer(player: Player) {
        // TODO
        // 以下の実装では、1人参加ごとに新規チームを作成している
        // すなわち、各チームにプレイヤーが1人ずつしかいない
        // また、色は赤・青が交互に採用される
        val team = Team(listOf(TeamColor.RED, TeamColor.BLUE)[(teams.size - 1) % 2])
        team.addPlayer(player)
        teams.add(team)
        if (field == null)
            field = GameField(
                player.world,
                listOf(
                    District(
                        this, Location(Bukkit.getWorlds()[0], 70.0, 70.0, -110.0), teams[0]
                    )
                )
            )
    }

    private fun getPlayers(): List<Player> {
        return teams.flatMap { it.getPlayers() }
    }

    fun getTeam(player: Player): Team? {
        return teams.firstOrNull { team -> team.getPlayers().contains(player) }
    }

    fun broadcastMessage(msg: String) {
        getPlayers().forEach {
            it.sendMessage(msg)
        }
    }

    fun executeCommand(command: GameCommand, sender: CommandSender): Int {
        return state.executeCommand(command, sender)
    }

}