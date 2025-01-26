package jp.trap.conqest.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import jp.trap.conqest.Main
import jp.trap.conqest.game.GameCommand
import jp.trap.conqest.game.GameTimerManager
import jp.trap.conqest.game.nite.NormalNite
import net.kyori.adventure.text.Component
import org.bukkit.entity.*

class CommandConQest(val plugin: Main) : Commands.Command {


    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("conqest")
            .requires { it.sender.isOp }
            .executes { ctx ->
                ctx.source.sender.sendMessage(
                    Component.text("conQest ${plugin.pluginMeta.version} (c) 2025 traP")
                )
                0
            }.then(
                io.papermc.paper.command.brigadier.Commands.literal("join").executes { ctx ->
                    plugin.gameManager.requestOpeningGame()?.executeCommand(GameCommand.JOIN, ctx.source.sender)
                        ?: run {
                            ctx.source.sender.sendMessage("現在、空いているフィールドがないため、ゲームを開始できません")
                        }
                    0
                }
            )
            .then(
                io.papermc.paper.command.brigadier.Commands.literal("summon_nite").then(
                    io.papermc.paper.command.brigadier.Commands.argument("name", StringArgumentType.string())
                        .executes { ctx ->
                            if ((ctx.source.sender is Player).not()) return@executes 0
                            val player = ctx.source.sender as Player
                            val arg = ctx.getArgument("name", String::class.java)
                            val game = plugin.gameManager.getGame(player)
                            game?.addNite(NormalNite(player.location, arg, player, plugin), player)
                                ?: player.sendMessage("You are not a game!")
                            0
                        }
                )
            )
            .build()
    }
}