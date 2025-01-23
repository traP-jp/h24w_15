package jp.trap.conqest.game

import org.bukkit.Location

// TODO Teamも乗せる
class District(val core: DistrictCore) {
    fun isContained(location: Location): Boolean {
        return false
    }
}