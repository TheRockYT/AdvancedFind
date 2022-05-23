package TheRockYT.AdvancedFind

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class AdvancedFindCommand(name: String?) : Command(name) {
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if(args!!.isEmpty()) {
            sender!!.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.info")))
        }else if(args[0].equals("version", true) || args[0].equals("updates", true)){
            val perm: String? = AdvancedFind.config?.getString("permission.updates")
            if(sender!!.hasPermission(perm)){
                sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.update.checking")))
                val oldState: Updater.CheckState? = AdvancedFind.updater?.state
                AdvancedFind.updater?.check()
                if (oldState == AdvancedFind.updater?.state) {
                    sender.sendMessage(
                        AdvancedFind.replacePlaceholder(
                            AdvancedFind.config?.get("messages.update." + oldState.toString().toLowerCase())
                        )
                    )
                }
            }else{
                sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.permission"))?.replace("%permission%", perm!!))
            }
        }else if(args[0].equals("reload", true)) {
            val perm: String? = AdvancedFind.config?.getString("permission.reload")
            if (sender!!.hasPermission(perm)) {
                sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.reload_start")))
                AdvancedFind.instance?.reload()
                sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.reload_end")))

            } else {
                sender.sendMessage(
                    AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.permission"))
                        ?.replace("%permission%", perm!!)
                )
            }
        }else if(args.size == 1){
            val perm: String? = AdvancedFind.config?.getString("permission.find")
            if (sender!!.hasPermission(perm)) {
                val player: ProxiedPlayer? = ProxyServer.getInstance().getPlayer(args[0])
                if(player == null){
                    sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.not_online")))
                }else {
                    sender.sendMessage(
                        AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.online"))
                            ?.replace("%server%", player.server.info.name)
                    )

                }
            } else {
                sender.sendMessage(
                    AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.permission"))
                        ?.replace("%permission%", perm!!)
                )
            }
        }else{
            sender!!.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.help")))
        }
    }

}
