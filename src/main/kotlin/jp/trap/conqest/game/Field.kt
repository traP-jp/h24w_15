package jp.trap.conqest.game

import jp.trap.conqest.util.Partition
import org.bukkit.Location
import org.bukkit.World

class Field(val center: Location, val coreLocations: List<Location>, val size: Pair<Int, Int>) {
    lateinit var districts: List<District>
    lateinit var graph: List<Set<Int>>
    private val x_min: Int = (center.x - size.first / 2).toInt()
    private val x_max: Int = (center.x + size.second / 2).toInt()
    private val y_min: Int = (center.z - size.first / 2).toInt()
    private val y_max: Int = (center.z + size.second / 2).toInt()

    constructor(
        center: Location,
        partition: Partition,
        coreLocations: List<Location>
    ) : this(center, coreLocations, partition.fieldSize) {
        val districtCount = partition.districts.size
        val locations: MutableList<MutableSet<Pair<Int, Int>>> = MutableList(size = districtCount) { mutableSetOf() }
        for (x in 0 until partition.fieldSize.first) for (y in 0 until partition.fieldSize.second) {
            val idx = partition.getDistrictIndex(x to y)!!
            locations[idx].add(x to y)
        }
        districts = (0 until districtCount).map { i ->
            District(i, locations[i], coreLocations[i])
        }
        graph = partition.graph
    }


    fun getDistrict(location: Pair<Int, Int>): District? {
        return districts.singleOrNull { district: District -> district.contains(location) }
    }

    fun getDistrict(location: Location): District? {
        return getDistrict(location.blockX - x_min to location.blockZ - y_min)
    }

    fun getWorld(): World {
        return center.world
    }
}
