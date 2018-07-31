package com.ampro.evemu.ribonucleic

import com.ampro.evemu.BIO_C
import com.ampro.evemu.CACHED_POOL
import com.ampro.evemu.organism.Organism
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.runBlocking

/**
 * Methods for transcription and translation (scoring) DNA sequences and Codons
 *
 * @author Jonathan Augustine
 * @since 3.0
 */

val READER_POOL = newFixedThreadPoolContext(3_000, "ReaderPool")

internal data class Pre_mRNA(var sequence: Array<RNA>) {
    val size: Int get() = sequence.size

    operator fun get(x: Int) : RNA = sequence[x]

    override fun toString(): String {
        var s = "["; sequence.forEach { s += it }; s += "]"
        return s
    }
}

internal class Ribosome(val sequence: Array<RNA>) {
    inline fun forStep(size: Int = BIO_C.codonSize,
                       action: (Int, Array<RNA>) -> Unit) {
        stepFrom(0, size, action)
    }

    inline fun stepFrom(index: Int = 0, size: Int = BIO_C.codonSize,
                        action: (Int, Array<RNA>) -> Unit) {
        val list = ArrayList<Array<RNA>>(BIO_C.startingDnaLength / 2)
        val smList = ArrayList<RNA>(sequence.size)
        for (i in (index * size) until sequence.size) {
            smList.add(sequence[i])
            if (smList.size == size) {
                list.add(smList.toTypedArray())
                smList.clear()
            }
        }
        list.add(smList.toTypedArray())
        list.filter { it.isNotEmpty() }.forEachIndexed(action)
    }
}

internal class RNAPolymerase(val sequence: Array<DNA>) {
    inline fun forStep(size: Int = BIO_C.codonSize,
                       action: (Int, Array<DNA>) -> Unit) {
        stepFrom(0, size, action)
    }

    inline fun stepFrom(index: Int = 0, size: Int = BIO_C.codonSize,
                        action: (Int, Array<DNA>) -> Unit) {
        val list = ArrayList<Array<DNA>>(BIO_C.startingDnaLength / 2)
        val smList = ArrayList<DNA>(sequence.size)
        for (i in (index * size) until sequence.size) {
            smList.add(sequence[i])
            if (smList.size == size) {
                list.add(smList.toTypedArray())
                smList.clear()
            }
        }
        list.add(smList.toTypedArray())
        list.forEachIndexed(action)
    }
}

/**
 * Transcribe a chromatid into an array of mRNA. While this is technically
 * inaccurate, it simplifies the process of scoring
 *
 * @param chromatid The chromatid to transcribe
 * @return An array of scored mRNA transcribed from the chromatid
 */
fun transcribe(chromatid: Chromatid) = runBlocking<Array<mRNA>> {
    fun Array<DNA>.isStart() : Boolean {
        BIO_C.startCodons.forEach {
            if (it.toDNA().contentEquals(this))
                return true
        }
        return false
    }
    fun Array<DNA>.isStop() : Boolean {
        BIO_C.stopCodons.forEach { if (it.toDNA().contentEquals(this)) return true }
        return false
    }
    //DNA -> pre-mRNA
    val p_rnalist = ArrayList<Pre_mRNA>()
    val poly = RNAPolymerase(chromatid.bases)
    val preRnaBuildList = ArrayList<RNA>(BIO_C.chromatidLengthRange.max/2)
    //Locate a start codon
    poly.forStep(size = BIO_C.codonSize) { index, bases ->
        //Start transcription
        if (bases.isStart()) {
            poly.stepFrom(index, size = BIO_C.codonSize) { _, triplet ->
                //Add each RNA base to the pre-mRNA builder list
                triplet.forEach { preRnaBuildList.add(it.toRNA()) }
                //Check for stop codon
                if (triplet.size == BIO_C.codonSize && triplet.isStop()) {
                    return@forStep
                } else if (triplet.size != BIO_C.codonSize){
                    //If the step isn't the right length we reached the end
                    //erase this
                    preRnaBuildList.clear()
                    return@forStep
                }
            }
        }
        if (preRnaBuildList.size > 0) {
            p_rnalist.add(Pre_mRNA(preRnaBuildList.toTypedArray()))
            preRnaBuildList.clear()
        }
    }

    //pre-mRNA -> mRNA
    val jobs = List(p_rnalist.size){ async(READER_POOL){ splice(p_rnalist[it]) } }
    return@runBlocking jobs.awaitAll().toTypedArray()
}

/**
 * TODO Clean introns and exons out of the pre_mRNA sequence
 *
 * @param pre_mRNA the Pre_mRNA to splice
 * @return the resulting mRNA strand
 */
internal fun splice(pre_mRNA: Pre_mRNA) : mRNA {
    val ribo = Ribosome(pre_mRNA.sequence)
    val list = ArrayList<Codon>()
    ribo.forStep(BIO_C.codonSize) { _, arrayOfRNAs ->
        list.add(Codon(arrayOfRNAs))
    }
    return mRNA(list.toTypedArray())
}

/**
 * Score an mRNA strand (in place of translating to amino acid placeholders)
 * and calculate it's weight.
 *
 * @param mRNA the mRNA to translate
 * @param scoredCodons An array of codons with Double scores
 */
fun translate(mRNA: mRNA, scoredCodons: Array<Codon>) {
    mRNA.score = mRNA.codons.let {
        var score = 0.0
        it.forEach { codon ->
            score += scoredCodons.get(codon)?.score ?: 0.0
        }
        return@let score
    }
    //(w = nCodons * codonScoreAvg * 0.1)
    mRNA.weight = mRNA.codons.size * mRNA.codons.let {
        var sum = 0.0
        it.forEach { codon ->  sum += scoredCodons.get(codon)?.score ?: 0.0 }
        return@let sum / it.size
    } * 0.1
}

/**
 * Calculate the score of each chromosome's chromatid's mRNA translation
 *
 * @param org The organism to score
 * @param scoredCodons Array of Scored codons
 * @return The calculated score as a Double
 */
fun score(org: Organism, scoredCodons: Array<Codon>) : Double {
    var score = 0.0

    val mRNAlist = ArrayList<mRNA>()

    org.chromosomes.forEach { zome ->
        zome.forEach { tid -> mRNAlist.addAll(transcribe(tid)) }
    }

    mRNAlist.forEach {
        translate(it, scoredCodons)
        score += it.score * it.weight
    }

    return score
}
