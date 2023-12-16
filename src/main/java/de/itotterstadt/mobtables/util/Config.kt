package de.itotterstadt.mobtables.util

import de.itotterstadt.mobtables.plugin
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class Config(id: String): YamlConfiguration() {

    private val file = File(plugin!!.dataFolder, "${id}.yml")

    init {
        load()
    }

    fun load() {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
//            plugin!!.saveResource(id, false)
        }

        try {
            load(file)
        } catch (e: IOException) {
            e.printStackTrace();
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace();
        }
    }

    fun save() {
        save(file)
    }


}