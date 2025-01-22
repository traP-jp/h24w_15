package jp.trap.conqest.util

import org.junit.jupiter.api.Test

class PartitionTest {
    @Test
    fun testGenerate() {
        for (size in 64..128 step 16) {
            val partition = Partition.generate(size to size, 16.0)
            assert(partition.isSuccess)
            println("partition($size):")
            println(partition.getOrDefault(partition.exceptionOrNull()?.message))
        }
    }

    @Test
    fun testGenerateWithInit() {
        for (size in 64..128 step 16) {
            val partition = Partition.generate(
                size to size, 16.0, listOf(Partition.District(size.toDouble() / 2 to size.toDouble() / 2, 32.0))
            )
            assert(partition.isSuccess)
            println("partition($size):")
            println(partition.getOrDefault(partition.exceptionOrNull()?.message))
        }
    }

    @Test
    fun testGenerateWithCount() {
        for (size in 64..128 step 16) {
            val partition = Partition.generateWithCount(size to size, (size / 16) * (size / 16))
            assert(partition.isSuccess)
            println("partition($size):")
            println(partition.getOrDefault(partition.exceptionOrNull()?.message))
        }
    }
}
