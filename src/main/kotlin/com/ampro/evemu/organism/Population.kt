package com.ampro.evemu.organism

import com.ampro.evemu.util.SequentialNamer
import com.ampro.evemu.util.random
import java.util.*

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
        var sum = 0.0
        this.forEach {
            (it.fitness - this.avgFitness).times(it.fitness - this.avgFitness)
        }
        return Math.sqrt(sum / this.size)
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

    override fun toString(): String = """
        $name | size=$size avgFit=$avgFitness fitDeviation=$stdDeviation
    """.trimIndent()

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

    override fun iterator(): Iterator<O> = population.iterator()

}

/**
 * Returns a array of Organisms produced by combining the chromatids of 2 parent organisms
 * @param numOffspring
 * @param maxChildrenPerPair
 * @return ArrayList<\Organism>
 *
internal fun matingSeason(numOffspring: Int, maxChildrenPerPair: Int,
                          minAge: Int): ArrayList<Organism> {

    //New List of organisms to be produced through reproduction
    val offspring = ArrayList<Organism>()
    //Add current population to temp List for manipulation
    val parents = ArrayList<Organism>()
    for (o in this.population)
        if (o.getAge() >= minAge) parents.add(o.copy())

    val pairs = ArrayList<IntArray>()

    for (i in 0 until ToolBox.permute(this.population.size, 2))
        for (k in i + 1 until this.population.size)
            pairs.add(intArrayOf(i, k))

    if (maxChildrenPerPair != 0) {
        val tempPairs = ArrayList<IntArray>()
        for (i in 0 until maxChildrenPerPair)
            for (k in pairs.indices)
                tempPairs.add(intArrayOf(pairs[k][0], pairs[k][1]))
        pairs.addAll(tempPairs)
    }


    if (EvolutionRunner.DEBUG_MATING) {
        for (i in pairs)
            println("Mating Pair  " + i[0] + "|" + i[1])
        println(pairs.size.toString() + " pairs")
    }

    var i = 0
    while (i < numOffspring) {


        if (pairs.isEmpty()) {
            for (int A = 0; A < ToolBox.permuteSize(this.population.size(), 2); A++)
            for (int k = A + 1; k < this.population.size(); k++)
            pairs.add(new int []{ A, k });
            if (maxChildrenPerPair != 0) {
                List < int[] > tempPairs = new ArrayList < > ();
                for (int B = 0; B < maxChildrenPerPair; B++)
                for (int k = 0; k < pairs.size(); k++)
                tempPairs.add(new int []{ pairs.get(k)[0], pairs.get(k)[1] });
                pairs.addAll(tempPairs);
            }

            if (EvolutionRunner.DEBUG_MATING) {
                for (int[] V : pairs)
                System.out.println("Mating Pairs  " + V[0] + "|" + V[1]);
                System.out.println(pairs.size() + " pairs");
            }
        }

        //get pair
        val pair: IntArray
        val index: Int
        try {
            index = Random().nextInt(pairs.size)
            pair = pairs[index]
        } catch (e: IllegalArgumentException) {
            i++
            continue
        }

        val parent_1 = this.population.get(pair[0])
        val parent_2 = this.population.get(pair[1])
        //if they aren't sexual
        if (!parent_1.isSexual() || !parent_2.isSexual()
            || parent_1.getAge() < minAge || parent_2.getAge() < minAge) {
            pairs.removeAt(index)
            if (i != 0)
                i--
            else
                i = 0
            i++
            continue
        }
        //If they dont have the same number of chromosomes they cant breed
        if (parent_1.getChromosomes().size != parent_2.getChromosomes().size) {
            if (i != 0)
                i--
            else
                i = 0
            i++
            continue
        }

        //generate child chromosomes from random parent chromatids
        val zygote = ArrayList<Chromosome>()
        for (a in 0 until parent_1.getChromosomes().size) {

            val tempCha = arrayOfNulls<Chromatid>(CHROMATID_per_CHROMOSOME)

            var k = 0
            while (k < CHROMATID_per_CHROMOSOME) {
                tempCha[k] = parent_1.getChromosomes().get(a).getChromatids()[Random().nextInt(
                        CHROMATID_per_CHROMOSOME)]
                tempCha[k + 1] = parent_2.getChromosomes().get(a).getChromatids()[Random().nextInt(
                        CHROMATID_per_CHROMOSOME)]
                k += 2
            }

            zygote.add(Chromosome(*tempCha))
        }

        //Setting up the child
        var child: Organism? = null
        //Generation
        var generation = 0
        if (parent_1.getGeneration() != null && parent_1.getGeneration() != null)
            generation = Math.max(parent_1.getGenNum(), parent_2.getGenNum()) + 1

        //Construct child Organism
        if (parent_1 is Producer)
            child = Producer("F$generation", null, zygote, true)
        else if (parent_1 is Predator)
            child = Predator("F$generation", null, zygote, true)
        else
            child = Organism("F$generation", null, zygote, true)

        //set parents
        child!!.setParents(parent_1, parent_2)

        //add organism to return
        offspring.add(child)
        i++

    }

    return offspring
}
*/
