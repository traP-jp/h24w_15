package jp.trap.conqest.commands

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import jp.trap.conqest.Main

class Commands(
    private val plugin: Main
) {
    interface Command {
        fun literalCommandNode(): LiteralCommandNode<CommandSourceStack>
    }

    fun init(): Result<Unit> {
        val lifecycleManager = plugin.lifecycleManager
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            it.registrar().register(CommandConQest(plugin).literalCommandNode())
            it.registrar().register(CommandGenerate(plugin).literalCommandNode())
        }
        return Result.success(Unit)
    }
}