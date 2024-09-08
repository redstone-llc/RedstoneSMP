package llc.redstone.redstonesmp.commands

import llc.redstone.redstonesmp.RedstoneSMP
import llc.redstone.redstonesmp.RedstoneSMP.Companion.groupData
import llc.redstone.redstonesmp.schemas.Group
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val playerMuteMap: HashMap<UUID, ArrayList<String>> = HashMap()

class ChatSettings: CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§cCHAT §8|§r §cUsage: /chatsettings <mute>")
            return true
        }
        if (sender !is Player) {
            sender.sendMessage("§cCHAT §8|§r §cOnly players can use this command.")
            return true
        }

        when (args[0]) {
            "mute" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /chatsettings mute <local, global, group, player> [player, group]")
                    return true
                }
                when (args[1]) {
                    "local" -> {
                        // Mute local chat
                        if (playerMuteMap.containsKey(sender.uniqueId)) {
                            playerMuteMap[sender.uniqueId] = playerMuteMap[sender.uniqueId]!!.apply {
                                if (contains("local")) {
                                    remove("local")
                                } else {
                                    add("local")
                                }
                            }
                        } else {
                            playerMuteMap[sender.uniqueId] = arrayListOf("local")
                        }
                    }
                    "group" -> {
                        // Mute group chat
                        if (args.size < 3) {
                            sender.sendMessage("§cUsage: /chatsettings mute group <group>")
                            return true
                        }
                        if (playerMuteMap.containsKey(sender.uniqueId)) {
                            playerMuteMap[sender.uniqueId] = playerMuteMap[sender.uniqueId]!!.apply {
                                if (contains(args[2])) {
                                    remove(args[2])
                                } else {
                                    add(args[2])
                                }
                            }
                        } else {
                            playerMuteMap[sender.uniqueId] = arrayListOf(args[2])
                        }
                    }
                    "player" -> {
//                        if (args.size < 3) {
//                            sender.sendMessage("§cUsage: /chatsettings mute player <player>")
//                            return true
//                        }
//                        // Mute player
//                        if (playerMuteMap.containsKey(sender.uniqueId)) {
//                            playerMuteMap[sender.uniqueId] = playerMuteMap[sender.uniqueId]!!.apply {
//                                if (contains(args[2])) {
//                                    remove(args[2])
//                                } else {
//                                    add(args[2])
//                                }
//                            }
//                        } else {
//                            playerMuteMap[sender.uniqueId] = arrayListOf(args[2])
//                        }

                        sender.sendMessage("§cCHAT §8|§r §cThis feature is not yet implemented.")
                    }
                    "global" -> {
                        // Mute global chat
                        if (playerMuteMap.containsKey(sender.uniqueId)) {
                            playerMuteMap[sender.uniqueId] = playerMuteMap[sender.uniqueId]!!.apply {
                                if (contains("global")) {
                                    remove("global")
                                } else {
                                    add("global")
                                }
                            }
                        } else {
                            playerMuteMap[sender.uniqueId] = arrayListOf("global")
                        }
                    }
                    else -> {
                        sender.sendMessage("§cUsage: /chatsettings mute <local, global, group, player> [player, group]")
                    }
                }
            }
        }
        return true
    }
}

class ChatSettingTabComplete: TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        p1: Command,
        p2: String,
        args: Array<String>
    ): MutableList<String>? {
        if (args.size == 1) {
            return mutableListOf("mute")
        }
        if (args.size == 2) {
            return mutableListOf("local", "global", "group", "player")
        }
        if (args.size == 3) {
            when (args[1]) {
                "group" -> {
                    val groups = groupData.filter { it.value.players.contains((sender as Player).uniqueId.toString()) }.keys
                    return groups.toMutableList()
                }
                "player" -> {
                    return RedstoneSMP.instance.server.onlinePlayers.map { it.name }.toMutableList()
                }
            }
        }
        return null
    }

}