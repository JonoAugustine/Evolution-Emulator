package com.ampro.evemu.emulation

import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.Population
import java.util.concurrent.ConcurrentHashMap

class Environment(val populations: ArrayList<Population<Organism>> = ArrayList(),
                  val resouces: ConcurrentHashMap<ResourceType, ResourcePool>
                  = ConcurrentHashMap()) {

    operator fun get(x: Int) : Population<Organism> = populations[x]
    operator fun set(x: Int, p: Population<Organism>) : Population<Organism> {
        val old = populations[x]
        populations[x] = p
        return old
    }
}

data class ResourcePool(val type: ResourceType = ResourceType.FOOD,
               val quantity: Double = 0.0)

enum class ResourceType { FOOD, SHELTER }
