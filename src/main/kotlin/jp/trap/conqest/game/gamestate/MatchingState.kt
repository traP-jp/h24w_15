package jp.trap.conqest.game.gamestate

import jp.trap.conqest.game.GameCommand
import jp.trap.conqest.game.GameManager
import jp.trap.conqest.game.GameState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MatchingState(private val gameManager: GameManager) : GameState(gameManager) {
    override fun executeCommand(command: GameCommand, sender: CommandSender): Int {
        if (command != GameCommand.JOIN) {
            super.executeCommand(command, sender)
            return 1
        } else {
            if(sender !is Player) {
                sender.sendMessage("このコマンドはプレイヤー専用です。")
                return 1
            }
            sender.sendMessage("conQestへようこそ。")
            gameManager.addPlayer(sender)
            gameManager.broadcastMessage("マッチングが完了しました。")
            gameManager.broadcastMessage("ゲームを開始します...")
            gameManager.setState(GameReadyState(gameManager))
            return 0
        }
    }
}