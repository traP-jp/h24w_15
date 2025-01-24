package jp.trap.conqest.game

import org.bukkit.Location
import org.bukkit.block.Block

class DistrictCore(district: District, location: Location) {
    private val block: Block = location.block

    init {
        block.type = district.getTeam().color.getConcreteMaterial()
    }

    fun onBreak(attackerTeam: Team) {
        block.type = attackerTeam.color.getConcreteMaterial()
    }
}