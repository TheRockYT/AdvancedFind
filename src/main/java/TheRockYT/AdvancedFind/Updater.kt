package TheRockYT.AdvancedFind

import com.google.gson.JsonParser
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.scheduler.ScheduledTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.TimeUnit

class Updater(private val version: String, private val api: String, private val plugin: Plugin) {
    var latest = "unknown"
        private set
    var latestDevelopment = "unknown"
        private set
    var state = CheckState.CHECKING
        private set
    private var scheduledTask: ScheduledTask? = null
    fun runUpdater() {
        if (scheduledTask != null) {
            scheduledTask!!.cancel()
        }
        scheduledTask = ProxyServer.getInstance().scheduler.schedule(plugin, {
            scheduledTask = null
            check()
            runUpdater()
        }, 30, TimeUnit.MINUTES)
    }

    fun check() {
        val oldState = state
        try {
            val jsonElement =
                JsonParser().parse(getURLContent(URL(api)))
            val jsonObject = jsonElement.asJsonObject
            latest = jsonObject["latest"].asString
            latestDevelopment = jsonObject["latest_development"].asString
            if (latest.equals(version, ignoreCase = true)) {
                state = CheckState.LATEST
            } else {
                if (latestDevelopment.equals(version, ignoreCase = true)) {
                    state = CheckState.LATEST_DEVELOPMENT
                } else {
                    val jsonArray = jsonObject["versions"].asJsonArray
                    for (jsonElement1 in jsonArray) {
                        val jsonObject1 = jsonElement1.asJsonObject
                        if (jsonObject1["version"].asString.equals(version, ignoreCase = true)) {
                            state = if (jsonObject1["release"].asBoolean) {
                                CheckState.OUTDATED
                            } else {
                                CheckState.OUTDATED_DEVELOPMENT
                            }
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            state = CheckState.CHECK_FAILED
            e.printStackTrace()
        }
        if (oldState != state) {
            AdvancedFind.broadcast(AdvancedFind.replacePlaceholder(AdvancedFind.config?.get("messages.update."+ AdvancedFind.updater?.state.toString().toLowerCase())), AdvancedFind.config?.getString("permission.updates"));
        }
    }

    fun stop() {
        if (scheduledTask != null) {
            scheduledTask!!.cancel()
            scheduledTask = null
        }
    }

    @Throws(Exception::class)
    fun getURLContent(url: URL): String? {
        val `is` = url.openConnection().getInputStream()
        val reader = BufferedReader(InputStreamReader(`is`))
        var lines: String? = ""
        var line: String? = null
        while (reader.readLine().also { line = it } != null) {
            lines += line
        }
        reader.close()
        return lines
    }

    enum class CheckState {
        LATEST_DEVELOPMENT, LATEST, OUTDATED, OUTDATED_DEVELOPMENT, CHECKING, CHECK_FAILED
    }
}