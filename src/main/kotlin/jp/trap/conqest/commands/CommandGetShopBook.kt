package jp.trap.conqest.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import jp.trap.conqest.game.item.ShopBook
import org.bukkit.entity.Player

class CommandGetShopBook : Commands.Command {
    override fun literalCommandNode(): LiteralCommandNode<CommandSourceStack> {
        return io.papermc.paper.command.brigadier.Commands.literal("getshopbook").executes(::executeGetShopBook)
            .build()
    }

    private fun executeGetShopBook(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        if (source.sender is Player) {
            val player = source.sender as Player
            player.inventory.addItem(ShopBook.itemStack)
        } else {
            source.sender.sendMessage("このコマンドはプレイヤーのみ使用できます")
        }
        return 1
    }
}