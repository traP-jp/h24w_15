package jp.trap.conqest.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.game.item.ChanceCard
import jp.trap.conqest.listeners.ListenerUsableItem
import org.bukkit.entity.Player

class CommandGetChanceCard : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands.literal("getchancecard").executes(::executeGetChanceCard)
            .build()
    }

    private fun executeGetChanceCard(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        if (source.sender is Player) {
            val player = source.sender as Player
            val itemStack = ChanceCard.item
            ListenerUsableItem.registry.plus(ChanceCard)
            player.inventory.addItem(itemStack)
        } else {
            source.sender.sendMessage("このコマンドはプレイヤーのみ使用できます")
        }
        return 1
    }
}