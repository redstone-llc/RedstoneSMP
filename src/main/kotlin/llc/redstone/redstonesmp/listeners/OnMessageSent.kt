package llc.redstone.redstonesmp.listeners

import llc.redstone.redstonesmp.RedstoneSMP.Companion.groupData
import llc.redstone.redstonesmp.RedstoneSMP.Companion.perms
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.commands.playerMuteMap
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent
import java.util.*


class OnMessageSent: Listener {
    @EventHandler
    fun onMessageSent(event: PlayerChatEvent ) {
        val player = event.player
        val message = event.message
        val players = player.server.onlinePlayers

        if (playerChatMap[player.uniqueId] == "local") {
            val nearbyPlayers = player.location.getNearbyPlayers(128.0)
            nearbyPlayers.forEach { nearbyPlayer ->
                if (playerMuteMap.containsKey(nearbyPlayer.uniqueId) && playerMuteMap[nearbyPlayer.uniqueId]!!.contains("local")) {
                    return@forEach
                }
                sendMessage(player, nearbyPlayer, message, "§aLOCAL")
            }
            sendToConsole(player, message, "§aLOCAL")
            event.isCancelled = true
            return
        }

        if (!playerChatMap.containsKey(player.uniqueId) || playerChatMap[player.uniqueId] == "global") {
            players.forEach { p ->
                if (playerMuteMap.containsKey(p.uniqueId) && playerMuteMap[p.uniqueId]!!.contains("global")) {
                    return@forEach
                }
                sendMessage(player, p, message)
            }
            sendToConsole(player, message)
            event.isCancelled = true
            return
        }

        val group = groupData[playerChatMap[player.uniqueId]]?: return
        group.players.forEach { playerId ->
            val p = Bukkit.getPlayer(UUID.fromString(playerId))
            if (p != null) {
                if (playerMuteMap.containsKey(p.uniqueId) && playerMuteMap[p.uniqueId]!!.contains(group.name)) {
                    return@forEach
                }
                sendMessage(player, p, message, "§a${group.name.uppercase()}")
            }
        }
        sendToConsole(player, message, "§a${group.name.uppercase()} ")
        event.isCancelled = true
    }
}

fun sendMessage(sender: Player, player: Player, message: String, prefix: String = "") {
    when {
        perms.playerInGroup(sender, "alt") -> {
            player.sendMessage(
                "$prefix §8●§r §7${sender.displayName} §8>>§r §7$message"
            )
        }
        else -> {
            player.sendMessage(
                "$prefix §8●§r ${sender.displayName} §8>>§r §f$message"
            )
        }
    }
}

fun sendToConsole(sender: Player, message: String, prefix: String = "") {
    val console: ConsoleCommandSender = Bukkit.getConsoleSender()
    when {
        perms.playerInGroup(sender, "alt") -> {
            console.sendMessage(
                "$prefix §8●§r §7${sender.displayName} §8>>§r §7$message"
            )
        }
        else -> {
            console.sendMessage(
                "$prefix §8● §r${sender.displayName} §8>>§r §f$message"
            )
        }
    }
}