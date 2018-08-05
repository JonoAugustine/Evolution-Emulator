package com.ampro.evemu.organism

import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.constants.Alphabet.O
import com.ampro.evemu.constants.Alphabet.P
import com.ampro.evemu.organism.ReproductiveType.CLONE
import com.ampro.evemu.organism.ReproductiveType.SEX
import com.ampro.evemu.util.IntRange
import com.ampro.evemu.util.SequentialNamer
import com.ampro.evemu.util.random
import com.ampro.evemu.util.sqr
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

internal val populationNamer = SequentialNamer(listOf(P,O,P), letterLength = 2, maxInt = 1_000)

/**
 * This class defines the object Population
 * A population consists of a group of Organisms
 *
 * @author Jonathan Augustine
 * @since 1.0
 */
data class Population<O: Organism>(val name: String = populationNamer.next(),
                                   val population: ArrayList<O> = ArrayList())
    : Iterable<O> {

    val size: Int get() = population.size

    val avgFitness: Double get() {
        var sum = 0.0
        this.forEach { sum += it.fitness }
        return sum / this.size
    }

    val stdDeviation: Double get() {
        val avg = this.avgFitness
        val size = this.size
        return Math.sqrt(
                (1.0 / size).times(this.let { pop ->
                    var sumVar = 0.0
                    pop.forEach { sumVar += (it.fitness - avg).sqr() }
                    return@let sumVar
                }))
    }

    val medianAge: Double get() = synchronized(this.population) {
        population.sortWith(Comparator { o1, o2 -> o1.age - o2.age })
        if (size % 2 == 0) {
            (this[(size - 1) / 1].age + this[size / 2].age) / 2.0
        } else {
            this[size / 2].age.toDouble()
        }
    }

    /**
     * Returns a List of organisms produced by cloning, sex, or both depending
     * on the organism ReproductiveType
     *
     * @param numOffspring The number of offspring to make
     * @return A List of the organisms produced
     */
    fun reproduce(numOffspring: Int, minAge: Int)
            : List<O> {
        return when {
            this[0].reproductiveType == CLONE -> {
                this.clone(numOffspring, minAge)
            }
            this[0].reproductiveType == SEX   -> {
                this.sex(numOffspring, minAge)
            }
            else                              -> {
                //this.sexAndClone(numOffspring, maxChildrenPerPair, minAge)
                listOf() //TODO
            }
        }
    }

    /**
     * @return A List of Organisms produced by cloning the members of the population
     */
    private fun clone(num: Int, minAge: Int) : List<O> {
        val legal = this.filter { it.age >= minAge }
        return if (legal.isEmpty()) listOf()
        else List (num) { legal[random(max = legal.size - 1)].clone() as O }
    }

    /**
     * TODO DOCS
     *
     * @param numOffspring
     * @param minAge
     * @return
     */
    private fun sex(numOffspring: Int, minAge: Int) : List<O> {
        //List of of-age organisms
        val parents = this.filter { it.age >= minAge && it.reproductiveType == SEX }
        if (parents.size < 2) return listOf()
        //List of organisms to be produced through reproduction
        val offspring = ArrayList<O>()

        val range = IntRange(0, parents.size - 1)

        val room = List(numOffspring) {
            async (FIXED_POOL) {
                var p1Dex: Int
                var p2Dex: Int
                do {
                    p1Dex = range.random()
                    p2Dex = range.random()
                } while (p1Dex != p2Dex
                    && parents[p1Dex].chromosomes.size != parents[p2Dex].chromosomes.size)
                //Mate and add offspring to the list
                parents[p1Dex].sex(parents[p2Dex]) as O
            }
        }
        return offspring.apply { runBlocking { addAll(room.awaitAll()) } }
    }

    /**
     * Kills random Organisms from the population below the given fitness
     *
     * @return The killed Organisms
     */
    fun cull(toll: Int, fitCutoff: Double = avgFitness-(2*stdDeviation)): ArrayList<O> {
        // Pull all organisms that may be killed and place in temp array, kill
        // randoms. Put remaining in temp array back into population
        val temp = ArrayList<O>()
        val snapped = ArrayList<O>()
        return synchronized(population) {
            for (i in 0 until population.size) {
                if (population[i].fitness < fitCutoff) {
                    temp.add(population.removeAt(i))
                }
            }
            for (j in 0 until toll) {
                if (temp.size != 0) {
                    temp.removeAt(random(max = temp.size)).also {
                        it.die()
                        snapped.add(it)
                    }
                } else break
            }
            population.addAll(temp)
            return@synchronized snapped
        }
    }

    /** Remove all dead Organisms from the population */
    fun purgeDead() { this.population.removeIf {!it.alive} }

    /** Sort the population list */
    fun sort(comparator: Comparator<O> = Comparator{o: O, o2: O -> o.compareTo(o2)}) {
        Collections.sort(this.population, comparator)
    }

    /** @return "name | size avgFitness fitnessStdDeviation" */
    override fun toString(): String
    = "$name | size=$size avgFit=$avgFitness fitDeviation=$stdDeviation"

    /**
     * Clear the population of all Organisms.
     *
     * @return The a list containing the cleared Organisms
     */
    fun clear() : ArrayList<O> {
        val old = ArrayList<O>().apply { addAll(population) }
        population.clear()
        return old
    }

    operator fun get(x: Int) : O = population[x]
    /**
     * Put an element in the given index
     *
     * @param x The index
     * @param o The Organism to set to
     * @return The previous element at the given index
     */
    operator fun set(x: Int, o: O) : O {
        val old = population[x]
        population[x] = o
        return old
    }
    fun add(o: O) { this.population.add(o) }
    fun addAll(collection: Collection<O>) { this.population.addAll(collection) }
    fun addAll(pop: Population<O>) { this.population.addAll(pop.population) }
    val isEmpty: Boolean get() = this.population.isEmpty()

    override fun iterator(): Iterator<O> = population.iterator()

}
