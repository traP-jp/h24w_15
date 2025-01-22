package jp.trap.conqest.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.Main
import jp.trap.conqest.game.ChanceCard
import org.bukkit.entity.Player

class CommandGetChanceCard(val plugin: Main) : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("getchancecard")
            .requires { it.sender.hasPermission("conqest.command.getchancecard") }
            .executes(::executeGetChanceCard)
            .build()
    }

    private fun executeGetChanceCard(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        if(source.sender is Player){
            var player = source.sender as Player
            player.inventory.addItem(ChanceCard.createChanceCard())
        }else {
            source.sender.sendMessage("このコマンドはプレイヤーのみ使用できます")
        }
        return 1
    }
}