package jp.trap.conqest.util

import jp.trap.conqest.game.Environment
import java.util.logging.Logger

class FlowHandler(private val logger: Logger, private val tasks: Collection<FlowTask>) {
    fun up(): Result<Unit> {
        tasks.forEachIndexed { index, task ->
            logger.info("Initializing task #${index}")
            task.up().onSuccess {
                logger.info("Initialized task #${index}")
            }.onFailure {
                logger.severe("Error initializing task #${index}")
                it.printStackTrace()
                return Result.failure(it)
            }
        }
        return Result.success(Unit)
    }

    fun down(): Result<Unit> {
        tasks.forEachIndexed { index, task ->
            logger.info("Finalizing task #${index}")
            task.down().onSuccess {
                logger.info("Finalizing task #${index}")
            }.onFailure {
                logger.severe("Error finalizing task #${index}")
                it.printStackTrace()
                return Result.failure(it)
            }
        }
        return Result.success(Unit)
    }
}

class FlowTask(
    val up: () -> Result<Unit>,
    val down: () -> Result<Unit>,
)