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

// TODO 仮 本体はNiteManager的なところに保持するべき
var nite: NormalNite? = null

class CommandConQest(val plugin: Main) : Commands.Command {
    var gameTimerManager: GameTimerManager = GameTimerManager(plugin)


    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands
            .literal("conqest")
            .requires{ it.sender.isOp }
            .executes { ctx ->
                ctx.source.sender.sendMessage(
                    Component.text("conQest ${plugin.pluginMeta.version} (c) 2025 traP")
                )
                0
            }.then(
                io.papermc.paper.command.brigadier.Commands.literal("join").executes { ctx ->
                    plugin.gameManager.executeCommand(GameCommand.JOIN, ctx.source.sender)
                }
            )
            .then(
                io.papermc.paper.command.brigadier.Commands.literal("summon_nite").then(
                    io.papermc.paper.command.brigadier.Commands.argument("name", StringArgumentType.string())
                        .executes { ctx ->
                            if ((ctx.source.sender is Player).not()) return@executes 0
                            val player = ctx.source.sender as Player
                            val arg = ctx.getArgument("name", String::class.java)
                            // TODO: 仮 本来はNiteManager的なところに保持するべき
                            nite = NormalNite(player.location, arg, plugin)
                            0
                        }
                )
            )
            .then(
                io.papermc.paper.command.brigadier.Commands.literal("timer")
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("start")
                            .then(
                                io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())
                                    .executes{ ctx ->
                                        val timerId = ctx.getArgument("timer-id", String::class.java)
                                        gameTimerManager.createAndStartTimer(timerId)
                                        0
                                    }
                            )
                    )
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("addplayer")
                            .then(
                                io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())
                                    .then(
                                        io.papermc.paper.command.brigadier.Commands.argument("player", ArgumentTypes.player())
                                            .executes{ ctx ->
                                                val timerId = ctx.getArgument("timer-id", String::class.java)
                                                val playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                                                val player = playerResolver.resolve(ctx.source).first()
                                                gameTimerManager.addPlayer(timerId, player)
                                                0
                                            }
                                    )
                            )
                    )
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("removeplayer")
                            .then(
                                io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())
                                    .then(
                                        io.papermc.paper.command.brigadier.Commands.argument("player", ArgumentTypes.player())
                                            .executes{ ctx ->
                                                val timerId = ctx.getArgument("timer-id", String::class.java)
                                                val playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                                                val player = playerResolver.resolve(ctx.source).first()
                                                gameTimerManager.removePlayer(timerId, player)
                                                0
                                            }
                                    )
                            )
                    )
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("pause")
                            .then(
                                io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())
                                    .executes{ ctx ->
                                        val timerId = ctx.getArgument("timer-id", String::class.java)
                                        gameTimerManager.pauseTimer(timerId)
                                        0
                                    }
                            )
                    )
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("restart")
                            .then(
                                io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())
                                    .executes{ ctx ->
                                        val timerId = ctx.getArgument("timer-id", String::class.java)
                                        gameTimerManager.restartTimer(timerId)
                                        0
                                    }
                            )
                    )
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("stop")
                            .then(
                                io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())
                                    .executes{ ctx ->
                                        val timerId = ctx.getArgument("timer-id", String::class.java)
                                        gameTimerManager.stopTimer(timerId)
                                        0
                                    }
                            )
                    )
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("remove")
                            .then(
                                io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())
                                    .executes{ ctx ->
                                        val timerId = ctx.getArgument("timer-id", String::class.java)
                                        gameTimerManager.removeTimer(timerId)
                                        0
                                    }
                            )
                    )
                    .then(
                        io.papermc.paper.command.brigadier.Commands.literal("removeall")
                            .executes{
                                gameTimerManager.removeAllTimer()
                                0
                            }
                    )
            )
            .build()
    }
}
//io.papermc.paper.command.brigadier.Commands.argument("timer-id", StringArgumentType.string())