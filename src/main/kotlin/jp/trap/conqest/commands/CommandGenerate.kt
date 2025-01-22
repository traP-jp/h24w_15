package jp.trap.conqest.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.Main
import net.kyori.adventure.text.Component

class CommandGenerate(val plugin: Main) : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("generate")
            .requires { it.sender.isOp }
            .executes { ctx ->
                ctx.source.sender.sendMessage(Component.text("TODO: show help"))
                0
            }
            .build()
    }

}