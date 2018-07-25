package com.ampro.evemu.dna

import com.ampro.util.ToolBox
import java.util.Comparator

enum class DNA : Comparable<DNA> {
    A, T, C, G;
    val comparator: Comparator<DNA> = Comparator { x, y ->
        return@Comparator when(x) {
            A -> when(y) {
                A -> 0
                else -> 1
            }
            T -> when(y) {
                A -> -1
                T -> 0
                else -> 1
            }
            C -> when(y) {
                A, T -> -1
                C -> 0
                else -> 1
            }
            G -> when(y) {
                A, T, C -> -1
                G -> 0
                null -> 1
            }
            null -> when(y) {
                null -> 0
                else -> -1
            }
        }
    }
}
enum class RNA { A, U, C, G }
enum class CodonFunction { NOTHING, STOP, START }

/** The length of all Codons */
var CODON_LENGTH: Int = 0

data class Codon(val bases: Array<DNA>) : Iterable<DNA> {

    companion object {
        var scoreComparator: Comparator<Codon> = Comparator { o1, o2 -> (100000000 * (o1.score - o2.score)).toInt() }

        /**Based on order ATCG */
        var baseComparator: Comparator<Codon> = object : Comparator<Codon> {
            override fun compare(o1: Codon, o2: Codon): Int {
                var retu = 0
                val b1 = o1.bases.trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val b2 = o2.bases.trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in b1.indices) {
                    retu = this.compareBase(b1[i], b2[i])
                    if (retu != 0)
                        return retu
                }
                return retu
            }

            /**
             * Returns -1,0,1 Based on order ATCG
             * @return
             */
            fun compareBase(s1: String, s2: String): Int {
                if (s1.length > 1 || s2.length > 1)
                    ToolBox.systmError("Comparator Failure", "$s1 vs $s2")
                var retu = 0
                if (s1 == "A") {
                    if (s2 == "A")
                        retu = 0
                    if (s2 == "T")
                        retu = -1
                    if (s2 == "C")
                        retu = -1
                    if (s2 == "G")
                        retu = -1
                } else if (s1 == "T") {
                    if (s2 == "A")
                        retu = 1
                    if (s2 == "T")
                        retu = 0
                    if (s2 == "C")
                        retu = -1
                    if (s2 == "G")
                        retu = -1
                } else if (s1 == "C") {
                    if (s2 == "A")
                        retu = 1
                    if (s2 == "T")
                        retu = 1
                    if (s2 == "C")
                        retu = 0
                    if (s2 == "G")
                        retu = -1
                } else if (s1 == "G") {
                    if (s2 == "A")
                        retu = 1
                    if (s2 == "T")
                        retu = 1
                    if (s2 == "C")
                        retu = 1
                    if (s2 == "G")
                        retu = 0
                } else {
                    ToolBox.systmError("Comparator Failure", "$s1 vs $s2")
                    retu = 0
                }
                return retu

            }
        }
    }

    var score: Float = 0f
    var function: CodonFunction = CodonFunction.NOTHING

    override fun equals(other: Any?): Boolean {
        return if (other is Codon) {
            other.bases.forEachIndexed { index, base ->
                if (this.bases[index] != base) {
                    return false
                }
            }
            false
        } else false
    }

    override fun iterator(): Iterator<DNA> = bases.iterator()

}


