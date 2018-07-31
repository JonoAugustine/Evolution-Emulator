package com.ampro.evemu.emulation

import com.ampro.evemu.BIO_C
import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.Population
import com.ampro.evemu.ribonucleic.Codon
import com.ampro.evemu.util.DoubleRange
import com.ampro.evemu.util.SequentialNamer
import com.ampro.evemu.util.slog
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.runBlocking
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

class SimpleEnvironment(populations: ArrayList<Population<Organism>> = ArrayList())
    : Environment(populations = populations) {

    val scoreMap = ConcurrentHashMap<String, Array<Codon>>()

    init {
        slog("[$name] Generating codon scores...")
        val time = measureTimeMillis {
            runBlocking {
                List(populations.size) {
                    async(FIXED_POOL) {
                        scoreMap.put(populations[it].name, scoreCodons())
                    }
                }.awaitAll()
            }
        }
        slog("[$name] ...done (time=${time/(1_000)})")
    }

}

/**
 * An environment contains the resource availabilities and a "difficulty" level
 * defining how often cataclysmic die-off events occur <br>
 *
 * @param name The name of the Environment (auto-generated if not given)
 * @param populations ArrayList of Populations (defaults to empty list)
 * @param resouces ResourcePools mapped to their ResourceType
 * @param risk The risk of cataclysmic events each "year"
 *
 * @author Jonathan Augustine
 * @since 1.0
 */
abstract class Environment( val name: String = enviroNamer.next(),
        val populations: ArrayList<Population<Organism>> = ArrayList(),
        val resouces: ConcurrentHashMap<ResourceType, ResourcePool>
        = ConcurrentHashMap(), var risk: Double = 0.0) {

    companion object { val enviroNamer = SequentialNamer("ENV") }

    protected fun scoreCodons(range: DoubleRange = BIO_C.codonScoreRange)
            : Array<Codon> = Array(BIO_C.codons.size) {
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
    val size: Int get() = this.populations.size

}

data class ResourcePool(val type: ResourceType = ResourceType.FOOD,
               val quantity: Double = 0.0)

enum class ResourceType { FOOD, SHELTER }
