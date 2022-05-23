package TheRockYT.AdvancedFind

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.io.File

class AdvancedFind : Plugin() {
    companion object {
        var config: Config? = null
        var updater: Updater? = null
        var version: String? = null
        var instance: AdvancedFind? = null

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
        version = description.version
        reload()

        cm.sendMessage("§2AdvancedFind §aloaded.")
        cm.sendMessage("§eThanks for using §2AdvancedFind")
    }
    fun reload() {
        ProxyServer.getInstance().pluginManager.unregisterCommands(this)
        
        config?.load()
        config?.set("info", "AdvancedFind v$version by TheRockYT.")
        config?.add("permission.updates", "AdvancedFind.version")

        config?.add("permission.reload", "AdvancedFind.reload")
        config?.add("permission.find", "AdvancedFind.find")

        config?.add("messages.info", "&2AdvancedFind &6> &2AdvancedFind &aby TheRockYT")

        config?.add("messages.permission", "&2AdvancedFind &6> &cYou need the permission \"%permission%\".")
        val help: ArrayList<String> = ArrayList()
        help.add("&2AdvancedFind &6> &cUse \"AdvancedFind version\" to check for updates.")
        help.add("&2AdvancedFind &6> &cUse \"AdvancedFind reload\" to reload the config.")
        help.add("&2AdvancedFind &6> &cUse \"AdvancedFind <player>\" to reload the config.")
        config?.add("messages.help", help)
        config?.add("messages.not_online", "&2AdvancedFind &6> &cThis player is not online.")
        config?.add("messages.online", "&2AdvancedFind &6> &cThis player is playing on %server%.")
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
        if (updater != null) {
            updater?.stop()
        }
        updater = version?.let { Updater(it, "https://therockyt.github.io/AdvancedFind/versions.json", this) }
        updater?.check()
        updater?.runUpdater()
        ProxyServer.getInstance().pluginManager.registerCommand(this, AdvancedFindCommand("AdvancedFind"))
        ProxyServer.getInstance().pluginManager.registerCommand(this, AdvancedFindCommand("find"))
        ProxyServer.getInstance().pluginManager.registerCommand(this, AdvancedFindCommand("afind"))
    }
    
    override fun onDisable() {
        // Plugin shutdown logic
    }
}