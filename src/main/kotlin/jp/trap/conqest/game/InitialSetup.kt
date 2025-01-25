package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerDropItemEvent

object InitialSetup {
    fun onEnableSetup() {
        for (world in Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            world.time = 6000
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            world.setStorm(false)
            world.isThundering = false
            world.setGameRule(GameRule.KEEP_INVENTORY, true)
            world.setGameRule(GameRule.MOB_GRIEFING, false)
            world.setGameRule(GameRule.DO_MOB_LOOT, false)
            world.setGameRule(GameRule.DO_FIRE_TICK, false)
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
            world.difficulty = Difficulty.EASY
            world.setGameRule(GameRule.FALL_DAMAGE, false)
        }
    }
}
