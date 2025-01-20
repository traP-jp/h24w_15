package jp.trap.conqest

import jp.trap.conqest.util.FlowHandler
import org.bukkit.plugin.java.JavaPlugin

const val figlet = """
                    ____            __
  _________  ____  / __ \___  _____/ /_
 / ___/ __ \/ __ \/ / / / _ \/ ___/ __/
/ /__/ /_/ / / / / /_/ /  __(__  ) /_
\___/\____/_/ /_/\___\_\___/____/\__/ conQest (c) 2025 traP
    """

class Main: JavaPlugin() {
    private lateinit var flowHandler: FlowHandler

    override fun onLoad() {
        logger.info(figlet)
        flowHandler = FlowHandler(
            logger,
            listOf()
        )
    }

    override fun onEnable() {
        flowHandler.up()
    }

    override fun onDisable() {
        flowHandler.down()
        logger.info("bye bye.")
    }
}