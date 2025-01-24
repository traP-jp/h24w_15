package jp.trap.conqest.commands

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.game.Wallet
import org.bukkit.entity.Player

class CommandPay : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("pay")
            .then(
                io.papermc.paper.command.brigadier.Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes { context ->
                        val amount = context.getArgument("amount", Int::class.java)
                        executePay(context, amount)
                    }
            )

            .build()
    }

    private fun executePay(context: CommandContext<CommandSourceStack>, amount: Int): Int {
        val source = context.source
        if (source.sender is Player) {
            Wallet.pay(source.sender as Player, amount)
        } else {
            source.sender.sendMessage("このコマンドはプレイヤーのみ使用できます")
        }
        return 1
    }
}