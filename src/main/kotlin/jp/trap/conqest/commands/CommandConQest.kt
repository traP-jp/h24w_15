package jp.trap.conqest.commands

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.Main
import net.kyori.adventure.text.Component

class CommandConQest(val plugin: Main) : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("conqest")
            .requires{ it.sender.isOp }
            .executes { ctx ->
                ctx.source.sender.sendMessage(
                    Component.text("conQest ${plugin.pluginMeta.version} (c) 2025 traP")
                )
                0
            }
            .build()
    }

}