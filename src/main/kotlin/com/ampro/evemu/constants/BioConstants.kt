package com.ampro.evemu.constants

import com.ampro.evemu.BIO_C
import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.ribonucleic.Codon
import com.ampro.evemu.ribonucleic.CodonFunction
import com.ampro.evemu.ribonucleic.RNA
import com.ampro.evemu.scan
import com.ampro.evemu.util.*
import com.ampro.evemu.util.IntRange
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.ArrayList

var CODON_LENGTH: Int = BIO_C.codonSize

/** A collection of biological BIO_C for all animals */
data class BioConstants(var name: String? = "unnamed $NOW", val codonSize: Int = 3,
                        val codonScoreRange: DoubleRange = DoubleRange(-1.0, 1.0),
                        val intronSignal: Pair<Array<RNA>, Array<RNA>>
                        = Pair(arrayOf(RNA.G, RNA.U), arrayOf(RNA.A, RNA.G)),
                        val chromatidLengthRange: IntRange = IntRange(1000, 1000),
                        val chromosomeSize: Int = 2, val initChromoNum: Int = 4,
                        val codons: Array<Codon>) {

    val startCodons: Array<Codon>
    val stopCodons: Array<Codon>

    init {
        name = name?: "BIOC $NOW"
        startCodons = codons.filter { it.isStart }.toTypedArray()
        stopCodons = codons.filter { it.isStop  }.toTypedArray()
    }

    /** Sets up all BIO_C */
    companion object {
        suspend fun build() : BioConstants {
            println("Set new Constants")
            println("Name: ")
            val n = scan.nextLine()
            val name = if (n.isBlank()) null else n

            println("Codon length: ")
            val codonLength = try {
                scan.nextLine().toInt()
            } catch (e: Exception) {
                System.err.println("Value empty, set to default: 3")
                3
            }
            CODON_LENGTH = codonLength
            val codonperms = async(FIXED_POOL) { codonPermutations(codonLength) }

            println("Codon score range 'min max': ")
            var split = scan.nextLine().split("[,\\s+]".toRegex())
            val scoreRange: DoubleRange = try {
                DoubleRange(split[0].toDouble(), split[1].toDouble())
            } catch (e: Exception) {
                System.err.println("Value empty, set to default: -1.0, 1.0")
                DoubleRange(-1.0, 1.0)
            }

            val codons = codonperms.await()

            println("Number of start codons: ")
            var nStartCodons: Int
            do {
                 try {
                    val len = scan.nextLine().toInt()
                    if (len > codons.size - 2) {
                        System.err.println("Too many! Choose a number between 1 and ${codons.size - 2}")
                        continue
                    } else {
                        nStartCodons = len
                        break
                    }
                } catch (e: Exception) {
                     System.err.println("Value empty, set to default: 1")
                     nStartCodons = 1
                     break
                 }
            } while (true)
            println("Number of stop codons: ")
            var nStopCodons: Int
            do {
                try {
                    val len = scan.nextLine().toInt()
                    if (len > codons.size - nStartCodons - 1) {
                        System.err.println("""Too many! Choose a number between
                            1 and ${codons.size - nStartCodons - 1}""".trimIndent())
                        continue
                    } else {
                        nStopCodons = len
                        break
                    }
                } catch (e: Exception) {
                    System.err.println("Value empty, set to default: 3")
                    nStopCodons = 3
                    break
                }
            } while (true)

            val sortJob = launch(FIXED_POOL){ codons.sortWith(Comparator { x, y ->
                return@Comparator when {
                    x.isStart -> when {
                        y.isStart -> 0
                        else      -> 1
                    }
                    x.isStop  -> when {
                        y.isStart -> -1
                        y.isStop  -> 0
                        else      -> 1
                    }
                    y.isStart -> -1
                    else      -> 0
                }
            }) }

            println("Intron Marker Length: ")
            val signalLength: Int = try {
                scan.nextLine().toInt()
            } catch (e: Exception) {
                System.err.println("Value empty, set to default: 2")
                2
            }

            val intronSignal = Pair(
                    Array(signalLength) { RNA.values()[random(0, 3)] },
                    Array(signalLength) { RNA.values()[random(0, 3)] }
            )

            println("Chromatid Length range 'min max': ")
            split = scan.nextLine().split("[,\\s+]".toRegex())
            val tidLengRange = try {
                IntRange(split[0].toInt(), split[1].toInt())
            } catch (e: Exception) {
                System.err.println("Value empty, set to default: 1,000, 1,000")
                IntRange(1_000, 1_000)
            }

            println("Chromosome Size: ")
            val zomeSize = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                System.err.println("Value empty, set to default: 2")
                2
            }

            println("Starting number of chromosomes: ")
            val initChromoNum = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                System.err.println("Value empty, set to default: 4"  )
                4
            }

            println("Set up Done\n")

            sortJob.join()

            println("Start Codons: ")
            var i = 0
            while (i in 0 until nStartCodons) {
                val temp = random(max = codons.size)
                if (!codons[temp].isStart) {
                    codons[temp].function = CodonFunction.START
                    println("\t${codons[temp]}")
                    i++
                }
            }

            println("Stop Codons: ")
            i = 0
            while (i in 0 until nStopCodons) {
                val temp = Random().nextInt(codons.size)
                if (!codons[temp].isStart && !codons[temp].isStop) {
                    codons[temp].function = CodonFunction.STOP
                    println("\t${codons[temp]}")
                    i++
                }
            }

            return BioConstants(name, codonLength, scoreRange, intronSignal,
                    tidLengRange, zomeSize, initChromoNum, codons)

        }
    }
}

/**
 * Generate all possible codons of the given size.
 *
 * @param codonLength The length of each codon
 */
fun codonPermutations(codonLength: Int) : Array<Codon> {
    val rna = arrayOf(RNA.A, RNA.U, RNA.C, RNA.G)
    val permutations: List<List<RNA>> = permute(rna, codonLength)
    val outArray = ArrayList<Codon>(permutations.size)
    permutations.forEach { arrayOfDNAs -> outArray.add(Codon(arrayOfDNAs)) }
    outArray.trimToSize()
    return outArray.toTypedArray()
}


