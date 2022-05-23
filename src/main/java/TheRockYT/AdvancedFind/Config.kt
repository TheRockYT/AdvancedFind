package TheRockYT.AdvancedFind

import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.IOException

class Config(val file: File) {
    var config: Configuration? = null

    fun load() {
        try {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(config, file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    operator fun set(key: String?, value: Any?) {
        config!![key] = value
    }

    fun add(key: String?, value: Any?) {
        if (!contains(key)) {
            config!![key] = value
        }
    }

    operator fun contains(key: String?): Boolean {
        return config!!.contains(key)
    }

    operator fun get(key: String?): Any {
        return config!![key]
    }

    fun getString(key: String?): String? {
        return if (contains(key)) {
            config!!.getString(key)
        } else null
    }

    fun getStringList(key: String?): List<String>? {
        return if (contains(key)) {
            config!!.getStringList(key)
        } else null
    }

    fun getInt(key: String?): Int? {
        return if (contains(key)) {
            config!!.getInt(key)
        } else null
    }

    fun getBool(key: String?): Boolean? {
        return if (contains(key)) {
            config!!.getBoolean(key)
        } else null
    }
    fun getValues(key: String?): MutableCollection<String>? {
        return config?.getSection(key)?.keys
    }
}