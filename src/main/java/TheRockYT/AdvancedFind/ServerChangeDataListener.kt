package TheRockYT.AdvancedFind

import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class ServerChangeDataListener : Listener {
    @EventHandler
    fun onPlayerSwitch(event: ServerConnectedEvent){
        if(AdvancedFind.data == true){
            AdvancedFind.data_config?.set("data."+event.player.uuid+".name", event.player.name)
            AdvancedFind.data_config?.set("data."+event.player.uuid+".last_server", event.server.info.name)
            AdvancedFind.data_config?.save()
        }

    }
}
