package de.itotterstadt.mobtables

import de.itotterstadt.mobtables.listeners.MobKillListener
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

var plugin: JavaPlugin? = null
class MobTables: JavaPlugin() {
    override fun onEnable() {
        plugin = this
        listen()
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
    }

    private fun listen() {
        server.pluginManager.registerEvents(MobKillListener(), this)
    }
}
