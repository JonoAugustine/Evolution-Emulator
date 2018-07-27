package com.ampro.evemu.organism

import com.ampro.evemu.BIO_CONSTANTS
import com.ampro.evemu.dna.Chromosome
import com.ampro.evemu.dna.generateChromosomes
import com.ampro.evemu.organism.ReproductiveType.CLONE
import com.ampro.evemu.util.SequentialNamer
import java.util.*


/**
 * A Class representing an Organism.
 */
abstract class Organism(val generation: Int,
                        val name: String = "${generation}_${organismNamer.next()}", //Gen#_ID
                        val parents: Array<Organism?>,
                        val reproductiveType: ReproductiveType = CLONE,
                        val chromosomes: Array<Chromosome> = generateChromosomes())
    : Comparable<Organism> {

    //Macro data
    var alive: Boolean = true
    var age: Int = 0

    /** How fit this organism is for its Environment */
    var fitness: Double = 0.0

    fun getChromosomeSize(): Int = BIO_CONSTANTS.chromosomeSize

    /** @return a random Int within the defined IntRange */
    fun getChromoatidLength(): Int = BIO_CONSTANTS.chromatidLengthRange.random()

    fun die() {
        this.alive = false
    }

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
        return "$name | ${if (alive) "alive" else "dead"}, age=$age, " +
                "Reproductive=$reproductiveType, fitness=$fitness | [$chromosomes]"
    }

}

val organismNamer: SequentialNamer = SequentialNamer()
