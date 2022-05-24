package TheRockYT.AdvancedFind

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.config.ServerInfo
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
                    if(AdvancedFind.data == true) {
                        var foundPlayer: Boolean = false
                        for (uuid in AdvancedFind.data_config?.getValues("data")!!) {
                            if (AdvancedFind.data_config?.getString("data." + uuid + ".name").equals(args[0], true)) {
                                foundPlayer = true
                                val message = TextComponent(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.last_server"))?.replace("%server%", AdvancedFind.data_config?.getString("data." + uuid + ".last_server")!!))
                                message.setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/advancedfind "+args[0]+" connect"))
                                message.setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.connect"))?.replace("%server%", AdvancedFind.data_config?.getString("data." + uuid + ".last_server")!!)).create()))
                                sender.sendMessage(message)
                                break;
                            }
                        }
                        if (!foundPlayer) {
                            sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.no_data")))
                        }
                    }
                }else {
                    val message = TextComponent(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.online"))?.replace("%server%", player.server.info.name))
                    message.setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/advancedfind "+player.name+" connect"))
                    message.setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.connect"))?.replace("%server%", player.server.info.name)).create()))
                    sender.sendMessage(message)
                }
            } else {
                sender.sendMessage(
                    AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.permission"))
                        ?.replace("%permission%", perm!!)
                )
            }
        }else if(args[1].equals("connect", true)) {
            val perm: String? = AdvancedFind.config?.getString("permission.connect")
            if (sender!!.hasPermission(perm)) {
                val player: ProxiedPlayer? = ProxyServer.getInstance().getPlayer(args[0])
                if(sender is ProxiedPlayer){
                    if(player == null){
                        sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.not_online")))
                        if(AdvancedFind.data == true){
                            var foundPlayer: Boolean = false
                            for(uuid in AdvancedFind.data_config?.getValues("data")!!){
                                if(AdvancedFind.data_config?.getString("data."+uuid+".name").equals(args[0], true)){
                                    foundPlayer = true
                                    val serverInfo: ServerInfo? = ProxyServer.getInstance().getServerInfo(AdvancedFind.data_config?.getString("data."+uuid+".last_server"))
                                    if(serverInfo == null){
                                        sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.not_exist")))

                                    }else{
                                        if(sender.server.info.name.equals(serverInfo.name)){
                                            sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.connected"))?.replace("%server%", serverInfo.name))

                                        }else{
                                            sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.connecting"))?.replace("%server%", serverInfo.name))

                                            sender.connect(serverInfo)
                                        }
                                    }

                                    break;
                                }
                            }
                            if(!foundPlayer){
                                sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.no_data")))
                            }
                        }
                    }else {

                        if(sender.server.info.name.equals(player.server.info.name)){

                            sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.connected"))?.replace("%server%", player.server.info.name))

                        }else{
                            sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.connecting"))?.replace("%server%", player.server.info.name))

                            sender.connect(player.server.info)
                        }
                    }
                    /*val message = TextComponent(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.online"))?.replace("%server%", player.server.info.name))
                    message.setClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "advancedfind "+player.name+" connect"))
                    message.setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.connect"))?.replace("%server%", player.server.info.name)).create()))
                    */
                }else{


                    sender.sendMessage(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.no_player")))
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
