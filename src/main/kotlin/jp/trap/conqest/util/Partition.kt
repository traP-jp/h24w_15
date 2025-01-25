package jp.trap.conqest.util

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class Partition(val fieldSize: Pair<Int, Int>, val districts: List<District> = emptyList()) {

    data class District(val center: Pair<Double, Double>, val size: Double) {

        fun distanceTo(target: Pair<Double, Double>): Double {
            return hypot(target.first - center.first, target.second - center.second)
        }

    }

    companion object {

        /**
         * Generate a new partition with the given field size and district size.
         * @param fieldSize the size of the field
         * @param districtSize the size of each district
         * @param init the initial districts
         * @return the generated partition
         */
        fun generate(
            fieldSize: Pair<Int, Int>, districtSize: Double, init: List<District> = emptyList()
        ): Result<Partition> {
            if (fieldSize.first <= 0 || fieldSize.second <= 0) {
                return Result.failure(IllegalArgumentException("fieldSize must be positive"))
            }
            if (districtSize <= 0) {
                return Result.failure(IllegalArgumentException("districtSize must be positive"))
            }
            if (init.any { it.size <= 0 }) {
                return Result.failure(IllegalArgumentException("districtSize must be positive"))
            }

            // Poisson Disk Sampling

            val districts = init.toMutableList()
            if (districts.isEmpty()) {
                val root =
                    Random.nextDouble(fieldSize.first.toDouble()) to Random.nextDouble(fieldSize.second.toDouble())
                districts.add(District(root, districtSize))
            }

            val candidates = districts.toMutableSet()
            sampling@ while (candidates.isNotEmpty()) {
                // randomly select a candidate and generate a new candidate around it
                val candidate = candidates.random()

                // attempt to generate a new candidate 30 times
                for (i in 0 until 30) {
                    val angle = Random.nextDouble(2 * Math.PI)
                    val distance = Random.nextDouble(districtSize, 2 * districtSize)
                    val next = District(
                        candidate.center.first + distance * cos(angle) to candidate.center.second + distance * sin(angle),
                        districtSize
                    )

                    if (next.center.first < 0 || next.center.first >= fieldSize.first) continue
                    if (next.center.second < 0 || next.center.second >= fieldSize.second) continue
                    if (districts.any { it.distanceTo(next.center) * 2 < it.size + next.size }) continue

                    // on success
                    districts.add(next)
                    candidates.add(next)
                    continue@sampling
                }

                // on failure
                candidates.remove(candidate)
            }

            return Result.success(Partition(fieldSize, districts))
        }

        /**
         * Generate a new partition with the given field size and district count.
         * @param fieldSize the size of the field
         * @param districtCount the count of districts
         * @param init the initial districts
         * @return the generated partition (may fail to match the count)
         */
        fun generateWithCount(
            fieldSize: Pair<Int, Int>, districtCount: Int, init: List<District> = emptyList()
        ): Result<Partition> {
            if (fieldSize.first <= 0 || fieldSize.second <= 0) {
                return Result.failure(IllegalArgumentException("fieldSize must be positive"))
            }
            if (districtCount < init.size) {
                return Result.failure(IllegalArgumentException("districtCount must be greater than or equal to the current count"))
            }
            if (init.any { it.size <= 0 }) {
                return Result.failure(IllegalArgumentException("districtSize must be positive"))
            }

            // Binary Search but widen the range a little each time
            var sizeLeft = 1
            var sizeRight = hypot(fieldSize.first.toDouble(), fieldSize.second.toDouble()).toInt()
            for (i in 0 until (districtCount + 64)) {
                val sizeMid = (sizeLeft + sizeRight) / 2
                generate(fieldSize, sizeMid.toDouble(), init).onSuccess {
                    if (it.districts.size == districtCount) {
                        return Result.success(it)
                    }
                    if (it.districts.size >= districtCount) {
                        sizeLeft = sizeMid
                        sizeRight += 1
                    } else {
                        sizeLeft = maxOf(1, sizeLeft - 1)
                        sizeRight = sizeMid
                    }
                }.onFailure {
                    return Result.failure(it)
                }
            }

            // fallback for the case where the Binary Search fails to find the exact count
            for (size in sizeLeft until (sizeLeft + 64)) {
                generate(fieldSize, size.toDouble(), init).onSuccess {
                    val generatingDistricts = it.districts.toMutableList()
                    while (generatingDistricts.size > districtCount) {
                        generatingDistricts.removeLast()
                    }
                    if (generatingDistricts.size == districtCount) {
                        return Result.success(Partition(fieldSize, generatingDistricts))
                    }
                }
            }

            return Result.failure(IllegalStateException("Failed to generate a partition with $districtCount districts"))
        }

    }

    val graph: List<Map<Int, Int>>

    private val grid: List<List<Int?>>
    private val distancesToRoad: List<List<Double>>

    init {
        if (fieldSize.first <= 0 || fieldSize.second <= 0) {
            throw IllegalArgumentException("fieldSize must be positive")
        }
        if (districts.any { it.size <= 0 }) {
            throw IllegalArgumentException("districtSize must be positive")
        }

        // construct a grid where each cell is assigned to the nearest district (normalized by district size)
        grid = List(fieldSize.first * 2 + 1) { i ->
            List(fieldSize.second * 2 + 1) { j ->
                districts.withIndex().minByOrNull {
                    it.value.distanceTo(i.toDouble() / 2 to j.toDouble() / 2) / it.value.size
                }?.index
            }
        }

        // construct a graph where each district is connected to its adjacent districts
        graph = List(districts.size) { mutableMapOf<Int, Int>() }.apply {
            for (i in 1 until grid.size step 2) {
                for (j in 1 until grid[i].size step 2) {
                    val districtIndex = grid[i][j] ?: continue
                    // check four adjacent cells
                    listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0).flatMap { (di, dj) ->
                        grid.getOrNull(i + di)?.getOrNull(j + dj)?.let { listOf(it) } ?: emptyList()
                    }.toSet().forEach {
                        if (it == districtIndex) return@forEach
                        val distanceToLine = calcDistanceToLineSegment(
                            i / 2.0 to j / 2.0,
                            districts[districtIndex].center,
                            districts[it].center
                        )
                        if (distanceToLine <= 4.0) {
                            get(districtIndex).merge(it, 1, Int::plus)
                            get(it).merge(districtIndex, 1, Int::plus)
                        }
                    }
                }
            }
        }

        // construct a distance map to the nearest road
        distancesToRoad = List(fieldSize.first) { i ->
            List(fieldSize.second) cell@{ j ->
                val districtIndex = getDistrictIndex(i to j) ?: return@cell Double.POSITIVE_INFINITY
                graph[districtIndex].flatMap { (neighbor, weight) ->
                    if (weight >= 5) listOf(neighbor) else emptyList()
                }.ifEmpty { listOf(districtIndex) }.minOf { neighbor ->
                    calcDistanceToLineSegment(
                        i.toDouble() + 0.5 to j.toDouble() + 0.5,
                        districts[districtIndex].center,
                        districts[neighbor].center
                    )
                }
            }
        }
    }

    /**
     * Get the district index at a grid position.
     * @param position the position to get the district index
     * @return the district index at the position
     */
    fun getDistrictIndex(position: Pair<Int, Int>): Int? =
        grid.getOrNull(position.first * 2 + 1)?.getOrNull(position.second * 2 + 1)

    /**
     * Check if a grid position is a center of a district.
     * @param position the position to check
     * @return true if the position is a center of a district
     */
    fun isCenter(position: Pair<Int, Int>): Boolean {
        val districtIndex = getDistrictIndex(position) ?: return false
        val (centerX, centerY) = districts[districtIndex].center
        return centerX.toInt() == position.first && centerY.toInt() == position.second
    }

    /**
     * Determine the border level at of a grid position.
     * @param position the position to get the border level
     * @return the border level at the position (0: inside, 1: bold, 2: thin)
     */
    fun getBorderLevel(position: Pair<Int, Int>): Int {
        val districtIndex = getDistrictIndex(position) ?: return 0

        val i = position.first * 2 + 1
        val j = position.second * 2 + 1
        // check four adjacent cells
        listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0).forEach { (di, dj) ->
            grid.getOrNull(i + di)?.getOrNull(j + dj)?.let {
                if (it != districtIndex) return 2
            }
        }
        // check four diagonal cells
        listOf(1 to 1, 1 to -1, -1 to 1, -1 to -1).forEach { (di, dj) ->
            grid.getOrNull(i + di)?.getOrNull(j + dj)?.let {
                if (it != districtIndex) return 1
            }
        }
        return 0
    }

    /**
     * Check if a grid position is in a road.
     * @param position the position to check
     * @param roadWidth the width of the road
     * @return true if the position is in a road
     */
    fun inRoad(position: Pair<Int, Int>, roadWidth: Double): Boolean =
        (distancesToRoad.getOrNull(position.first)?.getOrNull(position.second)
            ?: Double.POSITIVE_INFINITY) <= roadWidth / 2.0

    fun toString(roadWidth: Double): String = buildString {
        append("+-", "-".repeat(4 * fieldSize.second), "-+\n")
        for (x in 0 until fieldSize.first) {
            if (x > 0) append("| ", " ".repeat(4 * fieldSize.second), " |\n")
            append("| ")
            for (y in 0 until fieldSize.second) {
                val districtIndex = getDistrictIndex(x to y)
                val content = districtIndex?.toString()?.padStart(2, '0') ?: "??"
                append(
                    if (isCenter(x to y)) "[$content]"
                    else if (inRoad(x to y, roadWidth)) ":$content:"
                    else when (getBorderLevel(x to y)) {
                        2 -> "($content)"
                        1 -> "<$content>"
                        else -> " $content "
                    }
                )
            }
            append(" |\n")
        }
        append("+-", "-".repeat(4 * fieldSize.second), "-+\n")
    }

    override fun toString(): String = toString(3.0)

    private fun calcDistanceToLineSegment(
        p: Pair<Double, Double>, a: Pair<Double, Double>, b: Pair<Double, Double>
    ): Double {
        val ab = b.first - a.first to b.second - a.second
        val ap = p.first - a.first to p.second - a.second
        val bp = p.first - b.first to p.second - b.second
        if (ab.first * ap.first + ab.second * ap.second <= 0) {
            return hypot(ap.first, ap.second)
        }
        if (-ab.first * bp.first + -ab.second * bp.second <= 0) {
            return hypot(bp.first, bp.second)
        }
        return abs(ab.first * ap.second - ab.second * ap.first) / hypot(ab.first, ab.second)
    }
}
