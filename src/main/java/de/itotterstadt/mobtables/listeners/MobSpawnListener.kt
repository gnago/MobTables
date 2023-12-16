package de.itotterstadt.mobtables.listeners

import de.itotterstadt.mobtables.plugin
import de.itotterstadt.mobtables.util.Config
import de.itotterstadt.mobtables.util.range
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Piglin
import org.bukkit.entity.Slime
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.util.Vector
import kotlin.collections.LinkedHashMap

enum class SpawnConditionTypes {
    BIOME,
    BLOCK,
    TIME,
    ENTITY_LIMIT
}

class SpawnCondition(hashMap: LinkedHashMap<String, *>) {

    private val type = SpawnConditionTypes.valueOf((hashMap["type"] as String).uppercase(java.util.Locale.getDefault()))
    private val inverted = hashMap["invert"] as Boolean?
    private val data = hashMap

    private fun _valid(location: Location): Boolean {
        when (type) {
            SpawnConditionTypes.BIOME -> {
                val targetBiome = Biome.valueOf(data["biome"] as String)
                return targetBiome == location.block.biome
            }
            SpawnConditionTypes.BLOCK -> {
                val targetBlock = (data["filter"] as List<String>).map {
                    Material.valueOf(it)
                }

                val (pos1, pos2) = if (data.containsKey("offset1") && data.containsKey("offset2")) {
                    val offset1 = data["offset1"] as LinkedHashMap<String, *>
                    val offset2 = data["offset2"] as LinkedHashMap<String, *>
                    Pair(
                        Vector(
                            offset1["x"] as Int,
                            offset1["y"] as Int,
                            offset1["z"] as Int
                        ),
                        Vector(
                            offset2["x"] as Int,
                            offset2["y"] as Int,
                            offset2["z"] as Int
                        )
                    )
                } else {
                    val offset = data["offset"] as LinkedHashMap<String, *>?
                    val offsetX = offset?.get("x") as Int? ?: 0
                    val offsetY = offset?.get("y") as Int? ?: -1
                    val offsetZ = offset?.get("z") as Int? ?: 0
                    Pair(
                        Vector(offsetX, offsetY, offsetZ),
                        Vector(offsetX, offsetY, offsetZ)
                    )
                }

                for (x in pos1.x.toInt()..pos2.x.toInt()) {
                    for (y in pos1.y.toInt()..pos2.y.toInt()) {
                        for (z in pos1.z.toInt()..pos2.z.toInt()) {
                            if (targetBlock.contains(location.clone().add(x.toDouble(), y.toDouble(), z.toDouble()).block.type)) {
                                return true
                            }
                        }
                    }
                }
                return false

            }
            SpawnConditionTypes.TIME -> {
                val time = if ((data["useWorldTime"] as Boolean?) == true) location.world.gameTime else location.world.time

                if (data.containsKey("exact")) {
                    return time == data["exact"] as Long
                }

                if (data.containsKey("min") || data.containsKey("max")) {
                    var valid = true
                    if (data.containsKey("min")) {
                        if (time < data["min"] as Int) {
                            valid = false
                        }
                    }
                    if (data.containsKey("max")) {
                        if (time > data["max"] as Int) {
                            valid = false
                        }
                    }
                    return valid
                }

                if (data.containsKey("day")) {
                    val ninv = data["day"] as Boolean
                    if (ninv) {
                        return location.world.isDayTime
                    } else {
                        return !location.world.isDayTime
                    }
                }

                return false
            }
            SpawnConditionTypes.ENTITY_LIMIT -> {
                val filter = (data["filter"] as List<String>?)?.map {
                    EntityType.valueOf(it)
                }
                val range = data["range"] as Int * 1.0
                val limit = data["limit"] as Int

                val entities = location.world.getNearbyEntities(location, range, range, range) {
                    filter == null || filter.contains(it.type)
                }

                if (data["mode"] == "min") {
                    return entities.size >= limit
                }

                // mode == max
                return entities.size < limit
            }
        }
    }

    fun valid(location: Location): Boolean {
        if (inverted == true) {
            return !_valid(location)
        } else {
            return _valid(location)
        }
    }

}

open class Conditioned(hashMap: LinkedHashMap<String, *>) {
    val conditions = (hashMap["conditions"] as List<LinkedHashMap<String, *>>?)?.map {
        SpawnCondition(it)
    }

    fun conditionsMet(location: Location): Boolean {
        if (conditions == null) {
            return true
        }
        for (condition in conditions) {
            if (!condition.valid(location)) {
                return false
            }
        }
        return true
    }
}

class SpawnAttribute(hashMap: LinkedHashMap<String, *>): Conditioned(hashMap) {

    val values = hashMap["values"] as LinkedHashMap<String, *>

    fun apply(entity: Entity) {
        if (!conditionsMet(entity.location)) {
            return
        }

        for ((key, value) in values.entries) {
            when (key) {
                "IsImmuneToZombification" -> {
                    if (entity !is Piglin) {
                        plugin!!.logger.severe("mob ${entity.type} ist kein Piglin")
                        continue
                    }
                    entity.isImmuneToZombification = value as Boolean
                }
                "Size" -> {
                    if (entity !is Slime) {
                        plugin!!.logger.severe("mob ${entity.type} ist kein Slime oder MagmaCube")
                        continue
                    }
                    entity.size = range(value)
                }
            }
        }
    }

}

class SpawnEntry(hashMap: LinkedHashMap<String, *>): Conditioned(hashMap) {

    val type = EntityType.valueOf(hashMap["type"] as String)
    val chance = hashMap["chance"] as Double
    val rolls = hashMap["rolls"] ?: 1
    val attributes = (hashMap["attributes"] as List<LinkedHashMap<String, *>>?)?.map {
        SpawnAttribute(it)
    }

    fun spawn(location: Location) {
        if (!conditionsMet(location)) {
            return
        }

        for (i in 0 until range(rolls)) {
            val entity = location.world.spawnEntity(location, type, SpawnReason.CUSTOM)
            if (attributes != null) {
                for (attribute in attributes) {
                    attribute.apply(entity)
                }
            }
        }
    }

}

class SpawnPool(hashMap: LinkedHashMap<String, *>): Conditioned(hashMap) {

    val rolls = hashMap["rolls"] ?: 1
    val entries = (hashMap["entries"] as List<LinkedHashMap<String, *>>).map {
        SpawnEntry(it)
    }

    fun spawnEntities(location: Location) {
        for (entry in entries) {
            if (Math.random() <= entry.chance) {
                entry.spawn(location)
            }
        }
    }

    fun run(location: Location): Boolean {
        if (!conditionsMet(location)) {
            return false
        }
        for (i in 0 until range(rolls)) {
            spawnEntities(location)
        }
        return true
    }

}

class MobKillListener: Listener {

    private val spawnConfig = Config("spawning")

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onSpawn(event: EntitySpawnEvent) {
        if (event.entity.entitySpawnReason != SpawnReason.NATURAL) {
            return;
        }

        if (!spawnConfig.contains("keepVanillaSpawning") || !spawnConfig.getBoolean("keepVanillaSpawning")) {
            event.isCancelled = true;
        }

        val pools = spawnConfig.getList("pools")
        if (pools == null) {
            plugin!!.logger.severe("Biom pools undefiniert!")
            return
        }
        for (lPool in pools) {
            val pool = SpawnPool(lPool as LinkedHashMap<String, *>)
            if (pool.run(event.location)) {
                event.isCancelled = true;
            }
        }

        return
    }

}