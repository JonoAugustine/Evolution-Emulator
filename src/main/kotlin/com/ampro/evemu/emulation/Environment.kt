package com.ampro.evemu.emulation

import com.ampro.evemu.BIO_C
import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.dna.Codon
import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.Population
import com.ampro.evemu.util.DoubleRange
import kotlinx.coroutines.experimental.async
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SimpleEnvironment(populations: ArrayList<Population<Organism>>
                        = ArrayList()) : Environment(populations) {
    val scoreMap = ConcurrentHashMap<String, Array<Codon>>()

    init {
        populations.forEach { async (FIXED_POOL) {
            scoreMap.put(it.name, scoreCodons())
        }}
    }

}

/**An environment contains the resource availabilities and a "difficulty" level
 * defining how often cataclysmic die-off events occur <br>
 *
 * @author Jonathan Augustine
 */
abstract class Environment(val populations: ArrayList<Population<Organism>>
                           = ArrayList(),
                           val resouces: ConcurrentHashMap<ResourceType, ResourcePool>
                           = ConcurrentHashMap(), var danger: Double = 0.0) {

    protected fun scoreCodons(range: DoubleRange = BIO_C.codonScoreRange) : Array<Codon>
            = Array(BIO_C.codons.size) {
        BIO_C.codons[it].clone().also { it.score = range.random() }
    }

    operator fun get(x: Int) : Population<Organism> = populations[x]
    operator fun set(x: Int, p: Population<Organism>) : Population<Organism> {
        val old = populations[x]
        populations[x] = p
        return old
    }
    fun add(p: Population<Organism>) = populations.add(p)
    fun remove(x: Int) = populations.removeAt(x)

}

data class ResourcePool(val type: ResourceType = ResourceType.FOOD,
               val quantity: Double = 0.0)

enum class ResourceType { FOOD, SHELTER }
