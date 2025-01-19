package jp.trap.conqest

import org.bukkit.plugin.java.JavaPlugin

const val figlet = """
                    ____            __
  _________  ____  / __ \___  _____/ /_
 / ___/ __ \/ __ \/ / / / _ \/ ___/ __/
/ /__/ /_/ / / / / /_/ /  __(__  ) /_
\___/\____/_/ /_/\___\_\___/____/\__/ conQest (c) 2025 traP
    """

class Main: JavaPlugin() {
    override fun onEnable() {
        logger.info(figlet)
    }

    override fun onDisable() {

    }
}