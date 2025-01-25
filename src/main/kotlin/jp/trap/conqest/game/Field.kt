package jp.trap.conqest.game

import jp.trap.conqest.util.Partition
import org.bukkit.Location

class Field(
    private val center: Location,
    partition: Partition,
    private val coreLocations: List<Location>
) {
    val districts: List<District>

    init {
        val districtCount = partition.districts.size
        val locations: MutableList<MutableSet<Pair<Int, Int>>> = MutableList(size = districtCount) { mutableSetOf() }
        for (x in 0 until partition.fieldSize.first) for (y in 0 until partition.fieldSize.second) {
            val idx = partition.getDistrictIndex(x to y)!!
            locations[idx].add(x to y)
        }
        districts = (0 until districtCount).map { i -> District(locations[i], coreLocations[i]) }
    }

    private val size: Pair<Int, Int> = partition.fieldSize

    fun getDistrict(location: Pair<Int, Int>): District? {
        return districts.singleOrNull { district: District -> district.contains(location) }
    }

    fun getDistrict(location: Location): District? {
        return getDistrict(location.blockX to location.blockZ)
    }
}