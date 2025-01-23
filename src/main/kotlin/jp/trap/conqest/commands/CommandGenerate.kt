@file:Suppress("UnstableApiUsage")

package jp.trap.conqest.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.Main
import jp.trap.conqest.game.Field
import jp.trap.conqest.util.Partition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import io.papermc.paper.command.brigadier.Commands as BrigadierCommands

class CommandGenerate(val plugin: Main) : Commands.Command {

    private val previews = mutableMapOf<UUID, Field>()

    private fun setPreview(source: CommandSourceStack, partition: Partition): Int {
        val creatorId = source.executor?.uniqueId ?: UUID(0, 0)
        val field = Field(plugin, creatorId, source.location, partition)

        previews[creatorId] = field

        with(source.sender) {
            sendMessage(Component.text("-".repeat(40), NamedTextColor.GREEN))
            sendMessage(Component.text("Previewing at ${field.center}"))
            sendMessage(Component.text("  X: ${field.bottom()} to ${field.top()}"))
            sendMessage(Component.text("  Z: ${field.left()} to ${field.right()}"))
            sendMessage(Component.text("-".repeat(40), NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    private fun generate(source: CommandSourceStack): Int {
        source.sender.sendMessage(Component.text("TODO: show help", NamedTextColor.GOLD))
        return 0
    }

    private fun preview(source: CommandSourceStack): Int {
        Partition.generate(96 to 96, 16.0).onSuccess { return setPreview(source, it) }
            .onFailure { source.sender.sendMessage(Component.text(it.toString(), NamedTextColor.RED)) }
        return 0
    }

    private fun previewCount(source: CommandSourceStack): Int {
        Partition.generateWithCount(96 to 96, 40).onSuccess { return setPreview(source, it) }
            .onFailure { source.sender.sendMessage(Component.text(it.toString(), NamedTextColor.RED)) }
        return 0
    }

    private fun cancel(source: CommandSourceStack): Int {
        val creatorId = source.executor ?: UUID(0, 0)

        previews.remove(creatorId) ?: run {
            source.sender.sendMessage(Component.text("You are not previewing!", NamedTextColor.RED))
            return 0
        }

        source.sender.sendMessage(Component.text("Preview canceled.", NamedTextColor.DARK_RED))
        return Command.SINGLE_SUCCESS
    }

    private fun confirm(source: CommandSourceStack): Int {
        val creatorId = source.executor ?: UUID(0, 0)

        val field = previews[creatorId] ?: run {
            source.sender.sendMessage(Component.text("Need to preview first!", NamedTextColor.RED))
            return 0
        }
        previews.remove(creatorId)

        field.generate()

        with(source.sender) {
            sendMessage(Component.text("=".repeat(40), NamedTextColor.GREEN))
            sendMessage(Component.text("The field has been generated at ${field.center}!"))
            sendMessage(Component.text("=".repeat(40), NamedTextColor.GREEN))
        }
        return Command.SINGLE_SUCCESS
    }

    // TODO: receive parameters
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> =
        BrigadierCommands.literal("generate").requires { it.sender.isOp }.executes { generate(it.source) }
            .then(BrigadierCommands.literal("preview").executes { preview(it.source) })
            .then(BrigadierCommands.literal("previewcount").executes { previewCount(it.source) })
            .then(BrigadierCommands.literal("cancel").executes { cancel(it.source) })
            .then(BrigadierCommands.literal("confirm").executes { confirm(it.source) }).build()

}
