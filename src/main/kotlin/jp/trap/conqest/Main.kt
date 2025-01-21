package jp.trap.conqest

import jp.trap.conqest.commands.Commands
import jp.trap.conqest.game.GameManager
import jp.trap.conqest.listeners.Listeners
import jp.trap.conqest.util.FlowHandler
import jp.trap.conqest.util.FlowTask
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
    lateinit var gameManager: GameManager

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
        gameManager = GameManager(this)
    }

    override fun onEnable() {
        flowHandler.up()
    }

    override fun onDisable() {
        flowHandler.down()
        logger.info("bye bye.")
    }
}