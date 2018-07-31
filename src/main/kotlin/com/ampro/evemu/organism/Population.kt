package com.ampro.evemu.organism

import com.ampro.evemu.organism.ReproductiveType.CLONE
import com.ampro.evemu.organism.ReproductiveType.SEX
import com.ampro.evemu.util.Pair
import com.ampro.evemu.util.SequentialNamer
import com.ampro.evemu.util.permute
import com.ampro.evemu.util.random
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class defines the object Population
 * A population consists of a group of {@link Organism}s
 *
 * @author Jonathan Augustine
 */
data class Population<O: Organism>(val name: String = populationNamer.next(),
                      val population: ArrayList<O> = ArrayList()) : Iterable<O> {
    companion object {
        val populationNamer = SequentialNamer("POP", letterLength = 5)
    }

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
                    pop.forEach { org ->
                        sumVar += (org.fitness - avg) * (org.fitness - avg)
                    }
                    return@let sumVar
                }))
    }

    /**
     * Returns a List of organisms produced by cloning, sex, or both depending
     * on the organism ReproductiveType
     *
     * @param numOffspring
     * @param maxChildrenPerPair
     * @return ArrayList<\Organism>
     */
    fun reproduce(numOffspring: Int, maxChildrenPerPair: Int, minAge: Int)
            : List<O> {
        return when {
            this[0].reproductiveType == CLONE -> {
                this.clone(numOffspring, minAge)
            }
            this[0].reproductiveType == SEX   -> {
                this.sex(numOffspring, maxChildrenPerPair, minAge)
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
        val legal = this.filter{ it.age >= minAge }
        return if (legal.isEmpty()) listOf() else List (num) {
            legal[random(max = legal.size - 1)].clone() as O
        }
    }

    private fun sex(numOffspring: Int,
                    maxChildrenPerPair: Int = numOffspring/(size/2),
                    minAge: Int) : List<O> {

        //List of of-age organisms
        val parents = this.filter { it.age >= minAge && it.reproductiveType == SEX }
        //List of organisms to be produced through reproduction
        val offspring = ArrayList<O>()

        //Generate pairings bases of the indecies within the population
        val pairMap = ConcurrentHashMap<Pair<Int, Int>, AtomicInteger>()
        val permute = permute(Array(parents.size){it}, 2)
        if (permute.isEmpty()) return listOf()
        permute.forEach { pairMap[Pair(it[0], it[1])] = AtomicInteger(0) }
        val pairs = pairMap.keys.toMutableList()

        println()

        while (offspring.size <= numOffspring) {
            /** Get a pair, reset the list and map if we run out of pairs */
            fun getPair() : Pair<Int, Int> {
                while (true) {
                    if (pairs.isEmpty()) {
                        pairs.addAll(pairMap.keys)
                        pairMap.forEach { it.value.set(0) }
                    }
                    val random = random(max = pairs.size - 1)
                    val pair: Pair<Int, Int> = pairs[random]
                    if (pairMap[pair]!!.get() > maxChildrenPerPair) {
                        pairs.removeAt(random)
                    } else return pair
                }
            }

            //Get a pair of Organisms
            val pair = getPair()
            val p1 = parents[pair.left]
            val p2 = parents[pair.right]
            //Check if they can mate
            if (p1.chromosomes.size != p2.chromosomes.size) {
                //Remove the pair if they cannot mate
                pairMap.remove(pair)
                pairs.remove(pair)
                continue
            }

            //Mate and add offspring to the list
            offspring.add(p1.sex(p2) as O)
        }
        return offspring
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
