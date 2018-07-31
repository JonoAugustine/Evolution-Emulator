package com.ampro.evemu.ribonucleic

/**
 * A Gene is a sequence of Codons.
 *
 * @author Jonathan Augustine
 * @since 3.0
 */
class Gene(val codons: Array<Codon>) : Iterable<Codon> {

    init {

    }

    var weight: Float = 0f
    var score: Float = 0f

    override fun iterator(): Iterator<Codon> = codons.iterator()

}

class Trait(val genes: Array<Gene>) {

}
