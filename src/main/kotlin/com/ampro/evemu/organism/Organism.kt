package com.ampro.evemu.organism

import com.ampro.evemu.BIO_C
import com.ampro.evemu.ribonucleic.Chromosome
import com.ampro.evemu.ribonucleic.generateChromosomes
import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.organism.ReproductiveType.CLONE
import com.ampro.evemu.organism.ReproductiveType.SEX
import com.ampro.evemu.util.SequentialNamer
import com.ampro.evemu.util.random
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

class SimpleOrganism(generation: Int = 0,
                     name: String = organismNamer.next("F$generation"), //ORG_F#_ID
                     parents: Array<Organism>? = null,
                     reproductiveType: ReproductiveType = CLONE,
                     chromosomes: Array<Chromosome> = generateChromosomes())
    : Organism(generation, name, parents, reproductiveType, chromosomes) {

    override fun sex(partner: Organism): Organism {
        if (reproductiveType != SEX || partner.reproductiveType != SEX
            || partner !is SimpleOrganism) {
            throw IllegalArgumentException(
                    "Cannot sex a SimpleOrganism with ${partner.javaClass.simpleName}")
        }
        //generate child chromosomes from random parent chromatids
        //For each Chromosome (zome), generate a new chromosome from the
        // chromatids of the parents
        val zygote = Array (chromosomes.size) {zome ->
            Chromosome(Array (chromosomes[zome].size) { tid ->
                val rand = random(1, 2)
                if (rand == 1) {
                    chromosomes[zome].chromatids[tid]
                } else {
                    partner.chromosomes[zome].chromatids[tid]
                }
            })
        }
        return SimpleOrganism(Math.max(generation, partner.generation),
                parents = arrayOf(this, partner), reproductiveType = SEX,
                chromosomes = zygote)
    }

    override fun clone(): Organism = SimpleOrganism(generation, name, parents,
            reproductiveType, chromosomes)

}

/**
 * A Class representing an Organism.
 */
abstract class Organism(val generation: Int,
                        val name: String = organismNamer.next("F$generation"),
                        val parents: Array<Organism>? = null,
                        val reproductiveType: ReproductiveType = CLONE,
                        val chromosomes: Array<Chromosome> = generateChromosomes())
    : Comparable<Organism> {

    companion object {
        val organismNamer = SequentialNamer("ORG", letterLength = 4)
    }

    //Macro data
    var alive: Boolean = true
    var age: Int = 0

    /** How fit this organism is for its Environment */
    var fitness: Double = 0.0

    /** The number of base-pairs in the Organism genetic sequence */
    val baseCount: Int get() = runBlocking<Int> {
        //An array of async jobs counting the number of bases across all chromosomes
        val jobs = Array(chromosomes.size) { chromosomeIndex ->
            async(FIXED_POOL) {
                var count = 0
                chromosomes[chromosomeIndex].chromatids.forEach {
                    count += it.size
                }
                count
            }
        }
        jobs.let { var count = 0; it.forEach{ count+=it.await() }; count }
    }

    fun getChromosomeSize(): Int = BIO_C.chromosomeSize

    /** @return a random Int within the defined IntRange */
    fun getChromoatidLength(): Int = BIO_C.chromatidLengthRange.random()

    fun die() { this.alive = false }

    /**
     * @param partner The organism to breed with
     * @return A new organism resulting from the combination of this and the
     *          given organism
     */
    abstract fun sex(partner: Organism) : Organism

    /** @return an exact clone of this Organism */
    abstract fun clone(): Organism

    override fun compareTo(other: Organism): Int {
        return Comparator.comparing<Organism, Boolean> { it.alive }
            .thenComparing<Int> { it.generation }
            .thenComparingInt { it.age }
            .thenComparing<String> { it.name }
            .thenComparingDouble { it.fitness }
            .compare(this, other)
    }

    override fun toString(): String {
        return "$name | ${if (alive) "alive" else "dead"} age=$age, " +
                "Reproductive=$reproductiveType fitness=$fitness | " +
                "[${chromosomes.let { var s = ""; it.forEach {s += it}; s}}]"
    }

}

enum class ReproductiveType {SEX, CLONE, EITHER}
