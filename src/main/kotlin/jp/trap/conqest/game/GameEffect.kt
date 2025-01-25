package jp.trap.conqest.game

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object GameEffect {
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
    }
}
