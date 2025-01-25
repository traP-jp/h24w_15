package jp.trap.conqest.game

import jp.trap.conqest.Main
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Environment {
    fun onEnableSetup() {
        for (world in Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            world.time = 6000
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            world.setStorm(false)
            world.isThundering = false
            world.setGameRule(GameRule.KEEP_INVENTORY, true)
            world.setGameRule(GameRule.MOB_GRIEFING, false)
            world.setGameRule(GameRule.DO_MOB_LOOT, false)
            world.setGameRule(GameRule.DO_TILE_DROPS, false)
            world.setGameRule(GameRule.DO_FIRE_TICK, false)
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
            world.difficulty = Difficulty.EASY
            world.setGameRule(GameRule.FALL_DAMAGE, false)
            world.setGameRule(GameRule.PROJECTILES_CAN_BREAK_BLOCKS, false)

        }
    }

    fun update() {
        for (world in Bukkit.getWorlds()) {
            for (player in world.players) {
                player.foodLevel = 20
                player.saturation = 20f
                player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 2, 4, true, false, false))
            }
            for (entity in world.entities) {
                if (entity is LivingEntity) {
                    entity.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2, 0, true, false, false))
                }
            }
        }
        Main.instance.gameManager.getGames().forEach { game ->
            game.field.districts.forEach { district ->
                district.updateHp(game)
            }
        }
    }
}
