package llc.redstone.redstonesmp

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import llc.redstone.redstonesmp.commands.*
import llc.redstone.redstonesmp.listeners.OnMessageSent
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import org.json.simple.JSONObject
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.collections.HashMap


class RedstoneSMP : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: RedstoneSMP
            private set
        @JvmStatic lateinit var perms: Permission
            private set

        @JvmStatic val playerChatMap: HashMap<UUID, String> = HashMap()

        @JvmStatic val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        @JvmStatic lateinit var groupsFile: File
        @JvmStatic lateinit var groupData: HashMap<String, llc.redstone.redstonesmp.schemas.Group>
    }
    override fun onEnable() {
        // Plugin startup logic
        instance = this
        setupCommands()
        if (!setupPermissions()) {
            logger.severe("§cCHAT §8|§r Vault not found! Disabling plugin...")
            server.pluginManager.disablePlugin(this)
            return
        }

        saveGroups()

        server.pluginManager.registerEvents(OnMessageSent(), this)
    }

    private fun setupCommands() {
        getCommand("localchat")!!.setExecutor(LocalChat())
        getCommand("globalchat")!!.setExecutor(GlobalChat())
        getCommand("group")!!.setExecutor(Group())
        getCommand("group")!!.setTabCompleter(GroupTabCompleter())
        getCommand("chatsettings")!!.setExecutor(ChatSettings())
        getCommand("chatsettings")!!.setTabCompleter(ChatSettingTabComplete())
    }

    private fun saveGroups() {
        groupsFile = File(dataFolder, "groups.json")
        if (!groupsFile.exists()) saveResource(groupsFile.name, false);
        val json = FileReader(groupsFile).use {
            gson.fromJson(it, JsonObject::class.java)
        }
        groupData = HashMap()
        json.entrySet().forEach { (key, value) ->
            groupData[key] = llc.redstone.redstonesmp.schemas.Group.decode(value as JsonObject)
        }

    }

    private fun setupPermissions(): Boolean {
        val rsp: RegisteredServiceProvider<Permission>? =
            server.servicesManager.getRegistration(Permission::class.java)
        perms = rsp!!.getProvider()
        return perms != null
    }

    override fun onDisable() {
        // Plugin shutdown logic
        val json = gson.toJson(groupData)
        groupsFile.delete()
        Files.write(groupsFile.toPath(), json.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }
}
