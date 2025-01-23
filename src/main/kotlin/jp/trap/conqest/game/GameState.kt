package jp.trap.conqest.game

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
            for (i in 1..<5)
                gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                    gameManager.broadcastMessage("ゲーム開始まで" + (5 - i).toString() + "秒...")
                }, 20 * i.toLong())
            gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                gameManager.broadcastMessage("ゲーム開始!!")
                gameManager.setState(Playing(gameManager))
            }, 20 * 5)
        }
    }

    class Playing(private val gameManager: GameManager) : GameState(gameManager) {
    }
}