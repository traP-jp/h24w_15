package jp.trap.conqest.game

import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GameManager(val plugin: Plugin) {
    private val games: MutableList<Game> = mutableListOf()
    private val fields: MutableList<Field> = mutableListOf()
    fun requestOpeningGame(): Game? {
        return games.firstOrNull { game -> game.getStateType() == GameStates.MATCHING }
            ?: games.firstOrNull { game -> game.getStateType() == GameStates.BEFORE_GAME } ?: run {
                requestOpeningField()?.let { field ->
                    val newGame = Game(plugin, field)
                    games += newGame
                    newGame
                }
            }
    }

    private fun requestOpeningField(): Field? {
        return fields.firstOrNull { field -> games.all { game -> game.field != field } }
    }

    fun getGame(player: Player): Game? {
        return games.firstOrNull { game -> game.getPlayers().contains(player) }
    }

    fun addField(field: Field) {
        fields.add(field)
    }
}