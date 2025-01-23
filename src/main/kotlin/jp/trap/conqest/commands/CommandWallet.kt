package jp.trap.conqest.commands

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.game.Wallet
import org.bukkit.entity.Player

class CommandWallet : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("wallet")
            .then(
                io.papermc.paper.command.brigadier.Commands.literal("set")
                    .executes{ ctx ->
                        if((ctx.source.sender is Player).not()) return@executes 0
                        Wallet.setupScoreboard(ctx.source.sender as Player)
                        0
                    }
            )
            .then(
                io.papermc.paper.command.brigadier.Commands.literal("remove")
                    .executes{ ctx ->
                        if((ctx.source.sender is Player).not()) return@executes 0
                        Wallet.removeScoreboard(ctx.source.sender as Player)
                        0
                    }
            )
            .build()
    }

}