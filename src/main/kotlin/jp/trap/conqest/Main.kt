package jp.trap.conqest

import jp.trap.conqest.commands.Commands
import jp.trap.conqest.game.ChanceCard.Companion.createChanceCard
import jp.trap.conqest.listeners.Listeners
import jp.trap.conqest.util.FlowHandler
import jp.trap.conqest.util.FlowTask
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

const val figlet = """
                    ____            __
  _________  ____  / __ \___  _____/ /_
 / ___/ __ \/ __ \/ / / / _ \/ ___/ __/
/ /__/ /_/ / / / / /_/ /  __(__  ) /_
\___/\____/_/ /_/\___\_\___/____/\__/ conQest (c) 2025 traP
    """

class Main : JavaPlugin() {
    private lateinit var flowHandler: FlowHandler
    private lateinit var listeners: Listeners
    private lateinit var commands: Commands

    override fun onLoad() {
        logger.info(figlet)
        flowHandler = FlowHandler(
            logger,
            listOf(
                FlowTask(
                    {
                        listeners = Listeners(this)
                        listeners.init()
                    },
                    {
                        Result.success(Unit)
                    },
                ),
                FlowTask(
                    {
                        commands = Commands(this)
                        commands.init()
                    },
                    {
                        Result.success(Unit)
                    }
                )
            )
        )
    }

    override fun onEnable() {
        flowHandler.up()
    }

    override fun onDisable() {
        flowHandler.down()
        logger.info("bye bye.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("getchancecard", ignoreCase = true)) {
            if (sender is Player) {
                sender.inventory.addItem(createChanceCard())
            } else if (sender is ConsoleCommandSender) {
                sender.sendMessage("このコマンドはプレイヤーのみ使用できます")
            }
            return true
        }
        return false
    }
}