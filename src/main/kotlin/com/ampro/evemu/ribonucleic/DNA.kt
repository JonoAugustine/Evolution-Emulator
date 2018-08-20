package com.ampro.evemu.ribonucleic

/**
 * This file contains elements representing the basis of all
 * DNA-sequence-objects
 *
 * @author Jonathan Augustine
 * @since 3.0
 */

import com.ampro.evemu.BIO_C
import com.ampro.evemu.util.IntRange
import com.ampro.evemu.util.random
import java.util.*

enum class DNA {
    A, T, C, G;
    fun toRNA() : RNA = when(this) {
        DNA.A -> RNA.A
        DNA.T -> RNA.U
        DNA.C -> RNA.C
        DNA.G -> RNA.G
    }
    companion object {
        val comparator: Comparator<DNA> = Comparator { x, y ->
            return@Comparator when (x) {
                A -> when (y) {
                    A -> 0
                    else -> 1
                }
                T -> when (y) {
                    A -> -1
                    T -> 0
                    else -> 1
                }
                C -> when (y) {
                    A, T -> -1
                    C -> 0
                    else -> 1
                }
                G -> when (y) {
                    A, T, C -> -1
                    G -> 0
                    null -> 1
                }
                null -> when (y) {
                    null -> 0
                    else -> -1
                }
            }
        }
    }
}

data class Chromatid(val bases: Array<DNA>) : Iterable<DNA> {

    var score: Double = 0.0

    init {
        if (bases.size !in BIO_C.chromatidLengthRange) {
            throw IllegalArgumentException(
                "Chromatid size out of Range : ${BIO_C.chromatidLengthRange}"
            )
        }
    }

    val size: Int get() = bases.size

    operator fun get(x: Int) : DNA = bases[x]
    operator fun set(x: Int, base: DNA) { bases[x] = base }

    override fun toString(): String {
        val sb = StringBuilder()
        bases.forEach { sb.append(it.name) }
        return sb.toString()
    }

    override fun iterator(): Iterator<DNA> = bases.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chromatid

        if (!Arrays.equals(bases, other.bases)) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(bases)
        result = 31 * result + score.hashCode()
        return result
    }

}

data class Chromosome(val chromatids: Array<Chromatid>) : Iterable<Chromatid> {

    init {
        if (chromatids.size != BIO_C.chromosomeSize) {
            throw IllegalArgumentException("Chromosome missized")
        }
    }

    val size: Int get() = chromatids.size

    operator fun get(x: Int) : Chromatid = chromatids[x]
    operator fun set(x: Int, chromatid: Chromatid) { chromatids[x] = chromatid }

    override fun iterator(): Iterator<Chromatid> = chromatids.iterator()

    override fun toString(): String {
        val sb = StringBuilder("[")
        chromatids.forEach { sb.append("[$it]") }
        return sb.append("]").toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Chromosome
        if (!Arrays.equals(chromatids, other.chromatids)) return false
        return true
    }

    override fun hashCode(): Int = Arrays.hashCode(chromatids)

}

/**
 * Generates a random Array of chromosomes<br></br>
 * Each with chromatids of length within CHROMATID_LENGTH_RANGE[0]
 *
 * @param quantity The number of chromosomes to generate
 * @param size The number of chromatids per chromosome
 * @param chromaRange The size range of each chromatid
 * @return Array of Chromosomes
 */
fun generateChromosomes(quantity: Int = BIO_C.initChromoNum,
                        size: Int = BIO_C.chromosomeSize,
                        chromaRange: IntRange = BIO_C.chromatidLengthRange)
        : Array<Chromosome> {
    return Array(quantity) {Chromosome(Array(size) {
            Chromatid(Array(chromaRange.random()) { DNA.values()[random(0, 3)] })
        })
    }
}
