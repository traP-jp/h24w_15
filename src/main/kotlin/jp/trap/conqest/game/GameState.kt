package jp.trap.conqest.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

enum class GameStates {
    BEFORE_GAME, MATCHING, GAME_READY, PLAYING, AFTER_GAME,
}

sealed class GameState(private val game: Game) {

    abstract val type: GameStates

    open fun executeCommand(command: GameCommand, sender: CommandSender): Int {
        sender.sendMessage("このコマンドは現在実行できません。")
        return 1
    }

    class BeforeGame(private val game: Game) : GameState(game) {
        override val type: GameStates = GameStates.BEFORE_GAME
        override fun executeCommand(command: GameCommand, sender: CommandSender): Int {
            if (command != GameCommand.JOIN) {
                super.executeCommand(command, sender)
                return 1
            } else {
                if (sender !is Player) {
                    sender.sendMessage("このコマンドはプレイヤー専用です。")
                    return 1
                }
                sender.sendMessage("conQestへようこそ。")
                sender.sendMessage("対戦相手を待っています...")
                game.addPlayer(sender)
                game.setState(Matching(game))
                return 0
            }
        }
    }

    class Matching(private val game: Game) : GameState(game) {
        override val type: GameStates = GameStates.MATCHING
        override fun executeCommand(command: GameCommand, sender: CommandSender): Int {
            if (command != GameCommand.JOIN) {
                super.executeCommand(command, sender)
                return 1
            } else {
                if (sender !is Player) {
                    sender.sendMessage("このコマンドはプレイヤー専用です。")
                    return 1
                }
                sender.sendMessage("conQestへようこそ。")
                game.addPlayer(sender)
                game.broadcastMessage("マッチングが完了しました。")
                game.broadcastMessage("ゲームを開始します...")
                game.setState(GameReady(game))
                return 0
            }
        }
    }

    class GameReady(private val game: Game) : GameState(game) {
        override val type: GameStates = GameStates.GAME_READY

        init {
            for (i in 1 until 5) game.plugin.server.scheduler.runTaskLater(game.plugin, Runnable {
                game.broadcastMessage("ゲーム開始まで" + (5 - i).toString() + "秒...")
            }, 20 * i.toLong())
            game.plugin.server.scheduler.runTaskLater(game.plugin, Runnable {
                game.broadcastMessage("ゲーム開始!!")
                game.setState(Playing(game))
            }, 20 * 5)
        }
    }

    class Playing(private val game: Game) : GameState(game) {
        override val type: GameStates = GameStates.PLAYING
        private val gameTime: Long = 5 * 60
        private val initialCoin: Int = 100

        init {
            game.getPlayers().forEach { player: Player ->
                Wallet.setupScoreboard(player, initialCoin)
            }
            for (i in 0 until gameTime) game.plugin.server.scheduler.runTaskLater(game.plugin, Runnable {
                game.broadcastMessage("ゲーム終了まで" + (gameTime - i).toString() + "秒...")
            }, i * 20)
            game.plugin.server.scheduler.runTaskLater(game.plugin, Runnable {
                game.broadcastMessage("ゲーム終了!!")
                val result = game.judge() // TODO チーム情報を受け取り、メッセージに反映する
                val winner = result.maxBy { (_, v) -> v }.key
                fun teamToString(team: Team): String {
                    return team.getPlayers().joinToString(",") { Bukkit.getPlayer(it)!!.name }
                }
                game.broadcastMessage(
                    teamToString(winner) + "の勝利です")
                val loser = result.maxBy { (k, v) -> if(k == winner) 0 else v }.key
                game.getPlayers().forEach { player ->
                    player.showTitle(
                        Title.title(
                            Component.text(teamToString(winner) + "の勝利").color(TextColor.color(0x88FF88)),
                            Component.text().append(Component.text(teamToString(winner)).color(TextColor.color(0xFF8888)))
                                .append(Component.text(": " + result[winner] + "  vs  ").color(TextColor.color(0x888888)))
                                .append(Component.text(teamToString(loser)).color(TextColor.color(0x8888FF)))
                                .append(Component.text(": " + result[loser]).color(TextColor.color(0x888888))).build()
                        ),
                    )
                }
                game.setState(AfterGame(game))
            }, gameTime * 20)
        }
    }

    class AfterGame(private val game: Game) : GameState(game) {
        override val type: GameStates = GameStates.AFTER_GAME

        init {
            game.getPlayers().forEach { player: Player ->
                Wallet.removeScoreboard(player)
            }
            for (i in 1 until 5) game.plugin.server.scheduler.runTaskLater(game.plugin, Runnable {
                game.broadcastMessage("ロビー転送まで" + (5 - i).toString() + "秒...")
            }, 20 * i.toLong())
            game.plugin.server.scheduler.runTaskLater(game.plugin, Runnable {
                game.getPlayers().forEach { player ->
                    player.teleport(game.lobby)
                }
                game.setState(BeforeGame(game))
            }, 20 * 5)
        }
    }
}