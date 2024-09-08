package llc.redstone.redstonesmp.commands

import llc.redstone.redstonesmp.RedstoneSMP.Companion.perms
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.listeners.sendMessage
import llc.redstone.redstonesmp.listeners.sendToConsole
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class GlobalChat : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                val message = args.joinToString(" ")
                val players = sender.server.onlinePlayers
                players.forEach { player ->
                    sendMessage(sender, player, message, "")
                }
                sendToConsole(sender, message, "")
            } else {
                if (!playerChatMap.containsKey(sender.uniqueId) || playerChatMap[sender.uniqueId] == "global") {
                    sender.sendMessage("§cCHAT §8|§r §cYou are already in global chat.")
                } else {
                    sender.sendMessage("§cCHAT §8|§r §7You are now in global chat.")
                    playerChatMap[sender.uniqueId] = "global"
                }
            }
        } else {
            sender.sendMessage("§cCHAT §8|§r §cYou must be a player to use this command.")
        }
        return true
    }
}