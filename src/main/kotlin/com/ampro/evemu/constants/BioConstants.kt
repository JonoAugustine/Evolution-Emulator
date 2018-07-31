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
import java.util.*
import kotlin.collections.ArrayList

var CODON_LENGTH: Int = BIO_C.codonSize

/** A collection of biological BIO_C for all animals */
data class BioConstants(var name: String? = "unnamed $NOW",
                        val codonSize: Int,
                        val codonScoreRange: DoubleRange = DoubleRange(-1.0, 1.0),
                        val nStartCodons: Int = 3, val nStopCodons: Int = 1,
                        val intronSignal: Pair<Array<RNA>, Array<RNA>>
                        = Pair(arrayOf(RNA.G, RNA.U), arrayOf(RNA.A, RNA.G)),
                        val chromatidLengthRange: IntRange = IntRange(1000, 1000),
                        val chromosomeSize: Int = 2,
                        val startingChromosomes: Int = 4,
                        val startingDnaLength: Int = 60,
                        val minGeneLength: Int, val codons: Array<Codon>) {

    var startCodons: Array<Codon>
    var stopCodons: Array<Codon>

    init {
        name = name?: "unnamed $NOW"
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
            val codonLength = try{
                scan.nextLine().toInt()
            } catch (e: Exception) {
                System.err.println("Value empty, set to default: 3")
                3
            }

            CODON_LENGTH = codonLength
            val codonperms = async(FIXED_POOL) { codonPermutations(codonLength) }

            println("Score range 'min max': ")
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

            codons.sortWith(Comparator {x, y ->
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
            })

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
            val chromatidLengthRange = try {
                IntRange(split[0].toInt(), split[1].toInt())
            } catch (e: Exception) {
                System.err.println("Value empty, set to default: 1,000, 1,000")
                IntRange(1_000, 1_000)
            }

            println("Chromosome Size: ")
            val chromasomeSize = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                System.err.println("Value empty, set to default: 2")
                2
            }

            println("Starting number of chromosomes: ")
            val startingChromosomes = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                System.err.println("Value empty, set to default: 4"  )
                4
            }

            println("Starting DNA lenth: ")
            val startingDnaLength = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                System.err.println("Value empty, set to default: 1,000")
                1_000
            }

            println("Minimum Gene Length: ")
            val minGeneLength = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                System.err.println("Value empty, set to default: 1,000")
                1_000
            }

            println("Set up Done\n")

            println("Start Codons: ")
            for (i in 0 until nStartCodons) {
                val temp = Random().nextInt(codons.size)
                if (!codons[temp].isStart && !codons[temp].isStart) {
                    codons[temp].function = CodonFunction.START
                    println(codons[temp].toString())
                }
            }

            println("Stop Codons: ")
            for (i in 0 until nStopCodons) {
                val temp = Random().nextInt(codons.size)
                if (!codons[temp].isStart && !codons[temp].isStop) {
                    codons[temp].function = CodonFunction.STOP
                    println(codons[temp].toString() + "  ")
                }
            }

            val con = BioConstants(name, codonLength, scoreRange, nStartCodons,
                    nStopCodons, intronSignal, chromatidLengthRange, chromasomeSize,
                    startingChromosomes, startingDnaLength, minGeneLength, codons)


            println("Codon Score Range: ${con.codonScoreRange}")

            println("Chromatid Length Range: ${con.chromatidLengthRange}")

            println("Chromosome Size: ${con.chromosomeSize}")

            println("Starting Number of Chromosomes: ${con.startingChromosomes}")

            println("Minimum Gene Codon Length: ${con.minGeneLength}")

            return con
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


