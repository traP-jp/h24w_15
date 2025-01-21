package jp.trap.conqest.game.gamestate

import jp.trap.conqest.game.GameManager
import jp.trap.conqest.game.GameState

class GameReadyState(private val gameManager: GameManager) : GameState(gameManager) {
    init {
        for (i in 1..< 5)
            gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
                gameManager.broadcastMessage("ゲーム開始まで" + (5 - i).toString() + "秒...")
            }, 20 * i.toLong())
        gameManager.plugin.server.scheduler.runTaskLater(gameManager.plugin, Runnable {
            gameManager.broadcastMessage("ゲーム開始!!")
            gameManager.setState(PlayingState(gameManager))
        }, 20 * 5)
    }
}