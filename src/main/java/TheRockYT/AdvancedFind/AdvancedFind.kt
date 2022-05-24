package TheRockYT.AdvancedFind

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.io.File

class AdvancedFind : Plugin() {
    companion object {
        var config: Config? = null
        var data_config: Config? = null
        var updater: Updater? = null
        var version: String? = null
        var instance: AdvancedFind? = null
        var data: Boolean? = null

        fun replacePlaceholder(obj: Any?): String? {
            var finalString: String? = null
            if (obj is List<*>) {
                for (str in obj as List<*>) {
                    if (finalString == null) {
                        finalString = str as String?
                    } else {
                        finalString = finalString + "\n" + str as String?
                    }
                }
            }
            if (finalString == null) {
                finalString = obj as String?
            }
            if (finalString != null) {
                finalString = finalString.replace("&", "§")
                finalString = finalString.replace("%latest%", updater!!.latest)
                finalString = finalString.replace("%development%", updater!!.latestDevelopment)
                finalString = version?.let { finalString!!.replace("%version%", it) }
            }
            return finalString
        }
        fun broadcast(message: String?, permission: String?) {
            ProxyServer.getInstance().console.sendMessage(message)
            for (player in ProxyServer.getInstance().players) {
                if (player.hasPermission(permission)) {
                    player.sendMessage(message)
                }
            }
        }
    }
    override fun onEnable() {
        val cm: CommandSender = ProxyServer.getInstance().console
        cm.sendMessage("§aLoading §2AdvancedFind...")
        instance = this
        config = Config(File(dataFolder, "config.yml"))
        data_config = Config(File(dataFolder, "data.yml"))
        version = description.version
        reload()

        cm.sendMessage("§2AdvancedFind §aloaded.")
        cm.sendMessage("§eThanks for using §2AdvancedFind")
    }
    fun reload() {
        ProxyServer.getInstance().pluginManager.unregisterCommands(this)
        ProxyServer.getInstance().pluginManager.unregisterListeners(this)
        
        config?.load()
        config?.set("info", "AdvancedFind v$version by TheRockYT.")
        config?.add("data", false)
        config?.add("permission.updates", "AdvancedFind.version")

        config?.add("permission.reload", "AdvancedFind.reload")
        config?.add("permission.find", "AdvancedFind.find")
        config?.add("permission.connect", "AdvancedFind.connect")

        config?.add("messages.info", "&2AdvancedFind &6> &2AdvancedFind &aby TheRockYT")

        config?.add("messages.permission", "&2AdvancedFind &6> &cYou need the permission \"%permission%\".")
        val help: ArrayList<String> = ArrayList()
        help.add("&2AdvancedFind &6> &cUse \"AdvancedFind version\" to check for updates.")
        help.add("&2AdvancedFind &6> &cUse \"AdvancedFind reload\" to reload the config.")
        help.add("&2AdvancedFind &6> &cUse \"AdvancedFind <player>\" to find a player.")
        help.add("&2AdvancedFind &6> &cUse \"AdvancedFind <player> connect\" to connect to a server, ware the player was found.")
        config?.add("messages.help", help)
        config?.add("messages.not_online", "&2AdvancedFind &6> &cThis player is not online.")
        config?.add("messages.online", "&2AdvancedFind &6> &aThis player is playing on %server%. Click to connect.")
        config?.add("messages.connect", "&aClick to connect to %server%.")
        config?.add("messages.connecting", "&2AdvancedFind &6> &aSending you to %server%...")
        config?.add("messages.connected", "&2AdvancedFind &6> &cYou already connected to %server%.")
        config?.add("messages.last_server", "&2AdvancedFind &6> &aThis player played last time on %server%. Click to connect.")
        config?.add("messages.not_exist", "&2AdvancedFind &6> &cThis server does not exist.")
        config?.add("messages.no_data", "&2AdvancedFind &6> &aThis player played never before.")
        config?.add("messages.no_player", "&2AdvancedFind &6> &cYou need to be a player.")
        config?.add("messages.reload_start", "&2AdvancedFind &6> &aReloading &2AdvancedFind...")
        config?.add("messages.reload_end", "&2AdvancedFind &6> &2AdvancedFind &awas loaded.")
        val development_outdated = ArrayList<String>()
        development_outdated.add("&2AdvancedFind &6> &cYou are using an &4outdated &bdevelopment &cversion of &2AdvancedFind&c. &4Your version: v%version%. &aLatest release: v%latest%. &bLatest development: v%development%.")
        development_outdated.add("&2AdvancedFind &6> &aDownload the latest version at https://therockyt.github.io")
        val outdated = ArrayList<String>()
        outdated.add("&2AdvancedFind &6> &cYou are using an &4outdated &cversion of &2AdvancedFind&c. &4Your version: v%version%. &aLatest release: v%latest%. &bLatest development: v%development%.")
        outdated.add("&2AdvancedFind &6> &aDownload the latest version at https://therockyt.github.io.")
        config?.add("messages.update.outdated_development", development_outdated)
        config?.add("messages.update.outdated", outdated)
        config?.add(
            "messages.update.latest",
            "&2AdvancedFind &6> &aYou are using the latest release of &2AdvancedFind&a. &aYour version: v%version%. &aLatest release: v%latest%. &bLatest development: v%development%."
        )
        config?.add(
            "messages.update.latest_development",
            "&2AdvancedFind &6> &aYou are using the latest &bdevelopment &aversion of &2AdvancedFind&a. &bYour version: v%version%. &aLatest release: v%latest%. &bLatest development: v%development%."
        )
        config?.add("messages.update.checking", "&2AdvancedFind &6> &aChecking for updates...")
        config?.add("messages.update.check_failed", "&2AdvancedFind &6> &4Update check failed.")
        config?.save()
        data = config?.getBool("data")
        if (updater != null) {
            updater?.stop()
        }
        updater = version?.let { Updater(it, "https://therockyt.github.io/AdvancedFind/versions.json", this) }
        updater?.check()
        updater?.runUpdater()
        ProxyServer.getInstance().pluginManager.registerCommand(this, AdvancedFindCommand("AdvancedFind"))
        ProxyServer.getInstance().pluginManager.registerCommand(this, AdvancedFindCommand("find"))
        ProxyServer.getInstance().pluginManager.registerCommand(this, AdvancedFindCommand("afind"))
        if(data == true){

            ProxyServer.getInstance().pluginManager.registerListener(this, ServerChangeDataListener())
            data_config?.load()
        }
    }
    
    override fun onDisable() {
        // Plugin shutdown logic
    }
}