package llc.redstone.redstonesmp.commands

import llc.redstone.redstonesmp.RedstoneSMP.Companion.groupData
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.listeners.sendMessage
import llc.redstone.redstonesmp.listeners.sendToConsole
import llc.redstone.redstonesmp.schemas.Group
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class Group: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //create group, add player to group, remove player from group, list players in group, list groups, delete group, set name of group
        if (sender !is Player) {
            sender.sendMessage("§cCHAT §8|§r §cYou must be a player to use this command.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§cCHAT §8|§r §cUsage: /group <create|add|remove|list|listall|delete|setname|chat|leave>")
            return true
        }

        when (args[0]) {
            "leave" -> {
                if (args.size < 2) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group leave <group>")
                    return true
                }
                //leave group
                if (!groupData.containsKey(args[1])) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                    return true
                }
                val group = groupData[args[1]]?: return true
                if (!group.players.contains(sender.uniqueId.toString())) {
                    sender.sendMessage("§cCHAT §8|§r §cYou are not in this group.")
                    return true
                }
                if (group.owner == sender.uniqueId.toString()) {
                    sender.sendMessage("§cCHAT §8|§r §cYou cannot leave a group you own.")
                    return true
                }
                group.removePlayer(sender.uniqueId)
                sender.sendMessage("§cCHAT §8|§r §7aSuccessfully left group ${args[1]}.")
            }
            "create" -> { //Max of 3 groups per player
                if (args.size < 2) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group create <name>")
                    return true
                }
                if (args.size > 2) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup name cannot contain spaces.")
                    return true
                }
                if (groupData.containsKey(args[1]) || args[1] == "global" || args[1] == "local") {
                    sender.sendMessage("§cCHAT §8|§r §cGroup already exists or name is reserved.")
                    return true
                }
                val count = groupData.filter { it.value is Group && (it.value as Group).owner == sender.uniqueId.toString() }.count()
                if (count >= 3) {
                    sender.sendMessage("§cCHAT §8|§r §cYou can only own 3 groups.")
                    return true
                }
                groupData[args[1]] = Group(args[1], sender.uniqueId.toString(), listOf(sender.uniqueId.toString()))
                sender.sendMessage("§cCHAT §8|§r §7Successfully created group ${args[1]}.")
            }
            "add" -> {
                if (args.size < 3 || args.size > 3) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group add <player> <group>")
                    return true
                }
                //add player to group
                if (!groupData.containsKey(args[2])) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                    return true
                }
                val group = groupData[args[2]]?: return true
                if (group.owner != sender.uniqueId.toString()) {
                    sender.sendMessage("§cCHAT §8|§r §cYou do not own this group.")
                    return true
                }
                val target = Bukkit.getOfflinePlayer(args[1])?: return true
                if (group.players.contains(target.uniqueId.toString())) {
                    sender.sendMessage("§cCHAT §8|§r §cPlayer is already in group.")
                    return true
                }
                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage("§cCHAT §8|§r §cPlayer is not online.")
                    return true
                }
                group.addPlayer(Bukkit.getPlayer(args[1])!!.uniqueId)
                sender.sendMessage("§cCHAT §8|§r §7Successfully added ${args[1]} to group ${args[2]}.")
            }
            "remove" -> {
                //remove player from group
                if (args.size < 3 || args.size > 3) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group remove <player> <group>")
                    return true
                }
                if (!groupData.containsKey(args[2])) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                    return true
                }
                val group = groupData[args[2]]?: return true
                if (group.owner != sender.uniqueId.toString()) {
                    sender.sendMessage("§cCHAT §8|§r §cYou do not own this group.")
                    return true
                }
                val uuid = Bukkit.getOfflinePlayer(args[1]).uniqueId.toString()
                if (!group.players.contains(uuid) || group.owner == uuid) {
                    sender.sendMessage("§cCHAT §8|§r §cPlayer is not in group or is the owner.")
                    return true
                }
                group.removePlayer(Bukkit.getPlayer(args[1])!!.uniqueId)
                sender.sendMessage("§cCHAT §8|§r §7Successfully removed ${args[1]} from group ${args[2]}.")
            }
            "list" -> {
                //list players in group
                if (args.size < 2 || args.size > 2) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group list <group>")
                    return true
                }
                if (!groupData.containsKey(args[1])) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                    return true
                }
                val group = groupData[args[1]]?: return true
                sender.sendMessage("§cCHAT §8|§r §7Players in group ${args[1]}:")
                group.players.forEach {
                    sender.sendMessage("§8- §f${Bukkit.getOfflinePlayer(UUID.fromString(it)).name}")
                }
            }
            "listall" -> {
                //list groups you are in and players in those groups
                val groups = groupData.filter { it.value is Group && (it.value as Group).players.contains(sender.uniqueId.toString()) }
                if (groups.isEmpty()) {
                    sender.sendMessage("§cCHAT §8|§r §cYou are not in any groups.")
                    return true
                }
                groups.forEach { (name, group) ->
                    sender.sendMessage("§cCHAT §8|§r §7Players in group $name:")
                    (group as Group).players.forEach {
                        sender.sendMessage("§8- §f${Bukkit.getOfflinePlayer(UUID.fromString(it)).name}")
                    }
                }
            }
            "delete" -> {
                //delete group
                if (args.size < 2 || args.size > 2) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group delete <group>")
                    return true
                }
                if (!groupData.containsKey(args[1])) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                    return true
                }
                val group = groupData[args[1]]?: return true
                if (group.owner != sender.uniqueId.toString()) {
                    sender.sendMessage("§cCHAT §8|§r §cYou do not own this group.")
                    return true
                }
                groupData.remove(args[1])
                sender.sendMessage("§cCHAT §8|§r §7Successfully deleted group ${args[1]}.")
            }
            "setname" -> {
                //set name of group
                if (args.size < 3 || args.size > 3) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group setname <group> <name>")
                    return true
                }
                if (!groupData.containsKey(args[1])) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                    return true
                }
                val group = groupData[args[1]]?: return true
                if (group.owner != sender.uniqueId.toString()) {
                    sender.sendMessage("§cCHAT §8|§r §cYou do not own this group.")
                    return true
                }
                if (args[2] == "global" || args[2] == "local") {
                    sender.sendMessage("§cCHAT §8|§r §cName is reserved.")
                    return true
                }
                //What a great way to do this :sarcasm:
                groupData[args[2]] = group
                groupData.remove(args[1])
                (groupData[args[2]] as Group).setName(args[2])
                sender.sendMessage("§cCHAT §8|§r §7Successfully renamed group ${args[1]} to ${args[2]}.")
            }
            "chat" -> {
                //chat in group
                if (args.size < 2) {
                    sender.sendMessage("§cCHAT §8|§r §cUsage: /group chat <group> [message]")
                    return true
                }

                if (args.size == 2) {
                    if (!groupData.containsKey(args[1])) {
                        sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                        return true
                    }
                    val group = groupData[args[1]]?: return true
                    if (!group.players.contains(sender.uniqueId.toString())) {
                        sender.sendMessage("§cCHAT §8|§r §cYou are not in this group.")
                        return true
                    }
                    playerChatMap[sender.uniqueId] = args[1]
                    sender.sendMessage("§cCHAT §8|§r §7Now chatting in group ${args[1]}.")
                    return true
                }

                if (!groupData.containsKey(args[1])) {
                    sender.sendMessage("§cCHAT §8|§r §cGroup does not exist.")
                    return true
                }
                val group = groupData[args[1]]?: return true
                if (!group.players.contains(sender.uniqueId.toString())) {
                    sender.sendMessage("§cCHAT §8|§r §cYou are not in this group.")
                    return true
                }
                val message = args.drop(2).joinToString(" ")
                group.players.forEach {
                    sendMessage(sender, Bukkit.getPlayer(UUID.fromString(it))!!, message, "§a${group.name.uppercase()}")
                }
                sendToConsole(sender, message, "§a${group.name.uppercase()} ")
            }
            else -> {
                sender.sendMessage("§cCHAT §8|§r §cUsage: /group <create|add|remove|list|listall|delete|setname|chat|leave>")
            }
        }
        return true
    }
}

class GroupTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val groups = groupData.filter { it.value is Group && (it.value as Group).players.contains((sender as Player).uniqueId.toString()) }.keys
        if (args.size == 1) {
            return mutableListOf("leave", "create", "add", "remove", "list", "listall", "delete", "setname", "chat").toMutableList().filter {
                it.startsWith(args[0])
            }.toMutableList()
        }
        when (args[0]) {
            "leave" -> {
                if (args.size == 2) {
                    return groups.toMutableList()
                }
            }
            "create" -> {
                if (args.size == 2) {
                    return mutableListOf()
                }
            }
            "add" -> {
                if (args.size == 2) {
                    return Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
                }
                if (args.size == 3) {
                    return groups.toMutableList()
                }
            }
            "remove" -> {
                if (args.size == 2) {
                    return Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
                }
                if (args.size == 3) {
                    return groups.toMutableList()
                }
            }
            "list" -> {
                if (args.size == 2) {
                    return groups.toMutableList()
                }
            }
            "listall" -> {
                if (args.size == 2) {
                    return mutableListOf()
                }
            }
            "delete" -> {
                if (args.size == 2) {
                    return groups.toMutableList()
                }
            }
            "setname" -> {
                if (args.size == 2) {
                    return groups.toMutableList()
                }
                if (args.size == 3) {
                    return mutableListOf()
                }
            }
            "chat" -> {
                if (args.size == 2) {
                    return groups.toMutableList()
                }
            }
        }
        return mutableListOf()
    }
}