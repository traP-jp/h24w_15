package jp.trap.conqest.util

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class Partition(val fieldSize: Pair<Double, Double>, val districts: List<District> = emptyList()) {

    class District(val center: Pair<Double, Double>, val size: Double) {

        fun distanceTo(target: Pair<Double, Double>): Double {
            return hypot(target.first - center.first, target.second - center.second)
        }

    }

    private val grid: List<List<Int?>>

    init {
        if (fieldSize.first <= 0 || fieldSize.second <= 0) {
            throw IllegalArgumentException("fieldSize must be positive")
        }
        if (districts.any { it.size <= 0 }) {
            throw IllegalArgumentException("districtSize must be positive")
        }

        // construct a grid where each cell is assigned to the nearest district (normalized by district size)
        grid = List(fieldSize.first.toInt() * 2 + 1) { i ->
            List(fieldSize.second.toInt() * 2 + 1) { j ->
                districts.withIndex().minByOrNull {
                    it.value.distanceTo(i.toDouble() / 2 to j.toDouble() / 2) / it.value.size
                }?.index
            }
        }
    }

    /**
     * Generate a new partition with the given district size.
     * @param districtSize the size of each district
     * @return the generated partition
     */
    fun generate(districtSize: Double): Result<Partition> {
        if (districtSize <= 0) {
            return Result.failure(IllegalArgumentException("districtSize must be positive"))
        }

        // Poisson Disk Sampling

        val generatingDistricts = districts.toMutableList()
        if (generatingDistricts.isEmpty()) {
            val root = Random.nextDouble(fieldSize.first) to Random.nextDouble(fieldSize.second)
            generatingDistricts.add(District(root, districtSize))
        }

        val candidates = generatingDistricts.toMutableSet()
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
                if (generatingDistricts.any { it.distanceTo(next.center) * 2 < it.size + next.size }) continue

                // on success
                generatingDistricts.add(next)
                candidates.add(next)
                continue@sampling
            }

            // on failure
            candidates.remove(candidate)
        }

        return Result.success(Partition(fieldSize, generatingDistricts))
    }

    /**
     * Generate a new partition with the given district count.
     * @param districtCount the count of districts
     * @return the generated partition (may fail to match the count)
     */
    fun generateWithCount(districtCount: Int): Result<Partition> {
        if (districtCount < districts.size) {
            return Result.failure(IllegalArgumentException("districtCount must be greater than or equal to the current count"))
        }

        // Binary Search but widen the range a little each time
        var sizeLeft = 1
        var sizeRight = hypot(fieldSize.first, fieldSize.second).toInt()
        for (i in 0 until (districtCount + 64)) {
            val sizeMid = (sizeLeft + sizeRight) / 2
            generate(sizeMid.toDouble()).onSuccess {
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
            generate(size.toDouble()).onSuccess {
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

    /**
     * Get the district index at a grid position.
     * @param position the position to get the district index
     * @return the district index at the position
     */
    fun getDistrictIndex(position: Pair<Int, Int>): Int? = grid.getOrNull(position.first * 2 + 1)?.getOrNull(
        position.second * 2 + 1
    )

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

}
