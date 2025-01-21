package jp.trap.conqest.game

import org.bukkit.command.CommandSender

abstract class GameState(private val gameManager: GameManager) {
    open fun executeCommand(command: GameCommand, sender: CommandSender): Int {
        sender.sendMessage("このコマンドは現在実行できません。")
        return 1
    }
}