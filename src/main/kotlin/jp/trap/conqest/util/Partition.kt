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

    init {
        if (fieldSize.first <= 0 || fieldSize.second <= 0) {
            throw IllegalArgumentException("fieldSize must be positive")
        }
        if (districts.any { it.size <= 0 }) {
            throw IllegalArgumentException("districtSize must be positive")
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
            val root = Pair(Random.nextDouble(fieldSize.first), Random.nextDouble(fieldSize.second))
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
                    Pair(
                        candidate.center.first + distance * cos(angle), candidate.center.second + distance * sin(angle)
                    ), districtSize
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
}
