package com.ampro.evemu.constants

import com.ampro.evemu.dna.Codon
import com.ampro.evemu.dna.CodonFunction
import com.ampro.evemu.dna.DNA
import com.ampro.evemu.scan
import com.ampro.evemu.util.NOW
import com.ampro.evemu.util.permute
import kotlinx.coroutines.experimental.async
import java.util.*
import kotlin.collections.ArrayList

///-------------------------------General------------------------
/** The standard US alphabet in upper case  */
const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
/** digits 0 to 9  */
const val DIGITS = "0123456789"


/**The current highest first index letter for Population name generation */
var P_NEXT_NAME_LETTER_INDEX = 0
var P_NEXT_NAME__INDEX = 0
/**The current highest number for Population name generation */
var P_NEXT_NAME_NUMBER = 0

var CODON_LENGTH: Int = 0

/** A collection of biological BIO_CONSTANTS for all animals */
data class BioConstants(var name: String? = "unnamed $NOW",
                        val codonLength: Int,
                        val codonScoreRange: FloatRange = FloatRange(-1f, 1f),
                        val startCodons: Int = 3, val stopCodons: Int = 1,
                        val chromatidLengthRange: IntRange = IntRange(100, 100),
                        val chromasomeSize: Int = 2,
                        val startingChromosomes: Int = 4,
                        val startingDnaLength: Int = 60,
                        val minGeneLength: Int, val codons: Array<Codon>) {
    init {
        name = name?: "unnamed $NOW"
        CODON_LENGTH = codonLength
    }

    /** Sets up all BIO_CONSTANTS */
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
                3
            }
            CODON_LENGTH = codonLength
            val codonperms = async { codonPermutations(codonLength) }

            println("Score range 'min max': ")
            var split = scan.nextLine().split("[,\\s+]".toRegex())
            val scoreRange: FloatRange = try {
                FloatRange(split[0].toFloat(), split[1].toFloat())
            } catch (e: Exception) {
                FloatRange(-1f, 1f)
            }
            val codons = codonperms.await()
            println("Number of start codons: ")
            var startCodons: Int
            do {
                 try {
                    val L = scan.nextLine().toInt()
                    if (L > codons.size - 2) {
                        System.err.println("Too many! Choose a number between 1 and ${codons.size - 2}")
                        continue
                    } else {
                        startCodons = L
                        break
                    }
                } catch (e: Exception) {
                    startCodons = 3
                     break
                }
            } while (true)
            println("Number of stop codons: ")
            var stopCodons: Int
            do {
                try {
                    val L = scan.nextLine().toInt()
                    if (L > codons.size - startCodons - 1) {
                        System.err.println("""Too many! Choose a number between
                            1 and ${codons.size - startCodons - 1}""".trimIndent())
                        continue
                    } else {
                        stopCodons = L
                        break
                    }
                } catch (e: Exception) {
                    stopCodons = 1
                    break
                }
            } while (true)
            println("Chromatid Length range 'min max': ")
            split = scan.nextLine().split("[,\\s+]".toRegex())
            val chromatidLengthRange = try {
                IntRange(split[0].toInt(), split[1].toInt())
            } catch (e: Exception) {
                IntRange(100, 100)
            }

            println("Chromosome Size: ")
            val chromasomeSize = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                2
            }

            println("Starting number of chromosomes: ")
            val startingChromosomes = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                4
            }

            println("Starting DNA lenth: ")
            val startingDnaLength = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                1000
            }

            println("Minimum Gene Length: ")
            val minGeneLength = try {
                scan.nextLine().toInt()
            } catch (e: NumberFormatException) {
                1000
            }

            println("Set up Done\n")

            val con = BioConstants(name, codonLength, scoreRange, startCodons,
                    stopCodons, chromatidLengthRange, chromasomeSize,
                    startingChromosomes, startingDnaLength, minGeneLength, codons)


            print("Start Codons: ")
            for (i in 0..startCodons) {
                val temp = Random().nextInt(con.codons.size)
                if (!con.codons[temp].isStart && !con.codons[temp].isStart) {
                    con.codons[temp].function = CodonFunction.START
                    print(con.codons[temp].toString() + " ")
                }
            }

            print("\nStop Codons: ")
            for (i in 0 until con.stopCodons) {
                val temp = Random().nextInt(con.codons.size)
                if (!con.codons[temp].isStart && !con.codons[temp].isStop) {
                    con.codons[temp].function = CodonFunction.STOP
                    print(con.codons[temp].toString() + "  ")
                }
            }

            println("\nCodon Score Range: ${con.codonScoreRange}")

            println("Chromatid Length Range: ${con.chromatidLengthRange}")

            println("Chromosome Size: ${con.chromasomeSize}")

            println("Starting Number of Chromosomes: ${con.startingChromosomes}")

            println("Minimum Gene Codon Length: ${con.minGeneLength}")

            return con
        }
    }
}

/**
 * Generate all possible codons of the given size.
 * @param codonLength The length of each codon
 */
fun codonPermutations(codonLength: Int) : Array<Codon> {
    val dna = arrayOf(DNA.A, DNA.T, DNA.C, DNA.G)
    val permutations: List<List<DNA>> = permute(dna, codonLength)
    val outArray = ArrayList<Codon>(permutations.size)
    permutations.forEach { arrayOfDNAs -> outArray.add(Codon(arrayOfDNAs)) }
    outArray.trimToSize()
    return outArray.toTypedArray()
}


