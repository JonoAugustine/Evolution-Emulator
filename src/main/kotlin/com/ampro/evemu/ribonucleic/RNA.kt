package com.ampro.evemu.ribonucleic

import com.ampro.evemu.constants.CODON_LENGTH
import com.ampro.evemu.ribonucleic.CodonFunction.NOTHING
import com.ampro.evemu.util.elog
import java.util.*

enum class RNA {
    A, U, C, G;
    fun toDNA() : DNA = when(this) {
        RNA.A -> DNA.A
        RNA.U -> DNA.T
        RNA.C -> DNA.C
        RNA.G -> DNA.G
    }
    companion object {
        val comparator: Comparator<RNA> = Comparator { x, y ->
            return@Comparator when (x) {
                A -> when (y) {
                    A -> 0
                    else -> 1
                }
                U -> when (y) {
                    A -> -1
                    U -> 0
                    else -> 1
                }
                C -> when (y) {
                    A, U -> -1
                    C -> 0
                    else -> 1
                }
                G -> when (y) {
                    A, U, C -> -1
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
enum class CodonFunction { NOTHING, STOP, START }

fun Array<Codon>.get(codon: Codon) : Codon? {
    for (cd in this) {
        if (cd == codon) {
            return cd
        }
    }
    return null
}

/** An Object representing a Codon, a series of DNA Bases */
data class Codon(var bases: Array<RNA>, var function: CodonFunction = NOTHING)
    : Iterable<RNA>, Comparable<Codon> {

    constructor(bases: List<RNA>, function: CodonFunction = NOTHING)
            : this(bases.toTypedArray(), function)

    init {
        if (bases.size != CODON_LENGTH) {
            throw IllegalArgumentException(
                "Invalid codon length. must be $CODON_LENGTH is ${bases.size}"
            )
        }
    }

    var score:  Double = 0.0

    val isStop : Boolean get() = this.function == CodonFunction.STOP
    val isStart: Boolean get() = this.function == CodonFunction.START

    fun clone() : Codon = Codon(bases, function).also { it.score = this.score }

    fun toDNA() : Array<DNA> = Array(bases.size) { bases[it].toDNA() }

    companion object {
        var scoreComparator: Comparator<Codon> = Comparator { o1, o2 ->
            (100000000 * (o1.score - o2.score)).toInt()
        }

        /**Based on order AUCG */
        var baseComparator: Comparator<Codon> = Comparator { c1, c2 ->
            var res: Int
            c1.forEachIndexed { index, rna ->
                res = RNA.comparator.compare(rna, c2[index])
                if (res != 0) {
                    return@Comparator res
                }
            }
            return@Comparator 0
        }

    }

    override fun compareTo(other: Codon): Int = baseComparator.compare(this, other)

    operator fun get(x: Int) : RNA = bases[x]
    operator fun set(x: Int, base: RNA) { bases[x] = base }
    val length get() =  bases.size

    /** @return true if every DNA base is the same */
    override fun equals(other: Any?): Boolean {
        return if (other is Codon) {
            for ((index, base) in other.bases.withIndex()) {
                if (this.bases[index] != base) {
                    return false
                }
            }
            true
        } else false
    }

    override fun iterator(): Iterator<RNA> = bases.iterator()

    override fun toString(): String {
        val sb = StringBuilder()
        bases.forEach { sb.append(it) }
        return sb.toString()
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(bases)
        result = 31 * result + score.hashCode()
        result = 31 * result + function.hashCode()
        return result
    }

}

/**
 * A class representing a Strand of mRNA.
 * Holds both a score and a weight.
 * As of v3.0 the weight is calculated as (w = nCodons * codonScoreAvg * 0.1)
 *
 * @param codons An array of codons
 * @param score The score of the mRNA sequence   (default = 0)
 * @param weight The weight of the mRNA sequence (default = 0)
 *
 * @author Jonathan Augustine
 * @since 3.0
 */
data class mRNA(val codons: Array<Codon>, var score: Double = 0.0,
                var weight: Double = 0.0) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as mRNA

        if (!Arrays.equals(codons, other.codons)) return false
        if (score != other.score) return false

        return true
    }
    override fun hashCode(): Int {
        var result = Arrays.hashCode(codons)
        result = 31 * result + score.hashCode()
        return result
    }
}
