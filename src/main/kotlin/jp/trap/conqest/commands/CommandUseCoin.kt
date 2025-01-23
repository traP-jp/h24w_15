package jp.trap.conqest.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.game.Wallet
import org.bukkit.entity.Player

class CommandUseCoin : Commands.Command{
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("usecoin")
            .executes(::executeUseCoin)
            .build()
    }

    private fun executeUseCoin(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        if(source.sender is Player){
            Wallet.useCoin(source.sender as Player, 1)
        }else{
            source.sender.sendMessage("このコマンドはプレイヤーのみ使用できます")
        }
        return 1
    }
}