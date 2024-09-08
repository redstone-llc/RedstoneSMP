package llc.redstone.redstonesmp.commands

import llc.redstone.redstonesmp.RedstoneSMP.Companion.perms
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.listeners.sendMessage
import llc.redstone.redstonesmp.listeners.sendToConsole
import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LocalChat: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (args.isNotEmpty()) {
                val message = args.joinToString(" ")
                val players = sender.location.getNearbyPlayers(128.0)
                players.forEach { player ->
                    if (playerMuteMap.containsKey(player.uniqueId) && playerMuteMap[player.uniqueId]!!.contains("local")) {
                        return@forEach
                    }
                    sendMessage(sender, player, message, "§aLOCAL")
                }
                sendToConsole(sender, message, "§aLOCAL")
            } else {
                if (playerChatMap.containsKey(sender.uniqueId) && playerChatMap[sender.uniqueId] == "local") {
                    sender.sendMessage("§cCHAT §8|§r §cYou are already in local chat. You can use /globalchat to switch to global chat.")
                } else {
                    sender.sendMessage("§cCHAT §8|§r §7You are now in local chat.")
                    playerChatMap[sender.uniqueId] = "local"
                }
            }
        } else {
            sender.sendMessage("§cCHAT §8|§r §cYou must be a player to use this command.")
        }
        return true
    }
}