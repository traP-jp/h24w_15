package jp.trap.conqest.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed class GameState(private val gameManager: GameManager) {
    open fun executeCommand(command: GameCommand, sender: CommandSender): Int {
        sender.sendMessage("このコマンドは現在実行できません。")
        return 1
    }

    class BeforeGame(private val gameManager: GameManager) : GameState(gameManager) {
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
                gameManager.addPlayer(sender)
                gameManager.setState(Matching(gameManager))
                return 0
            }
        }
    }

    class Matching(private val gameManager: GameManager) : GameState(gameManager) {
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
                gameManager.addPlayer(sender)
                gameManager.broadcastMessage("マッチングが完了しました。")
                gameManager.broadcastMessage("ゲームを開始します...")
                gameManager.setState(GameReady(gameManager))
                return 0
            }
        }
    }

    class GameReady(private val gameManager: GameManager) : GameState(gameManager) {
        init {
            for (i in 1 until 5) gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                gameManager.broadcastMessage("ゲーム開始まで" + (5 - i).toString() + "秒...")
            }, 20 * i.toLong())
            gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                gameManager.broadcastMessage("ゲーム開始!!")
                gameManager.setState(Playing(gameManager))
            }, 20 * 5)
        }
    }

    class Playing(private val gameManager: GameManager) : GameState(gameManager) {
        val gameTime: Long = 5 * 60

        init {
            gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                gameManager.broadcastMessage("ゲーム終了!!")
                val winner = gameManager.judge() // TODO チーム情報を受け取り、メッセージに反映する
                gameManager.broadcastMessage(winner.toString() + "の勝利です")
                gameManager.getPlayers().forEach { player ->
                    player.showTitle(
                        Title.title(
                            Component.text(winner.toString() + "の勝利").color(TextColor.color(0x88FF88)),
                            Component.text().append(Component.text("Player1").color(TextColor.color(0xFF8888)))
                                .append(Component.text(": 100  vs  ").color(TextColor.color(0x888888)))
                                .append(Component.text("Player2").color(TextColor.color(0x8888FF)))
                                .append(Component.text(": 200").color(TextColor.color(0x888888))).build()
                        ),
                    )
                }
                gameManager.setState(AfterGame(gameManager))
            }, gameTime * 20)
        }
    }

    class AfterGame(private val gameManager: GameManager) : GameState(gameManager) {
        init {
            for (i in 1 until 5) gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                gameManager.broadcastMessage("ロビー転送まで" + (5 - i).toString() + "秒...")
            }, 20 * i.toLong())
            gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                gameManager.getPlayers().forEach { player ->
                    player.teleport(gameManager.lobby)
                }
                gameManager.setState(BeforeGame(gameManager))
            }, 20 * 5)
        }
    }
}