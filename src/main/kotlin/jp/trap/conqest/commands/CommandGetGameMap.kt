package jp.trap.conqest.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.game.GameManager
import org.bukkit.entity.Player

class CommandGetGameMap(private val gameManager: GameManager) : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands.literal("getgamemap").executes(::executeGetShopBook)
            .build()
    }

    private fun executeGetShopBook(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        if (source.sender is Player) {
            val player = source.sender as Player
            val game = gameManager.getGame(player)
            if (game == null) {
                player.sendMessage("ゲームに参加していません。")
                return 1
            }
            val mapItem = game.requestNewMap()
            player.inventory.addItem(mapItem)
        } else {
            source.sender.sendMessage("このコマンドはプレイヤーのみ使用できます")
        }
        return 1
    }
}