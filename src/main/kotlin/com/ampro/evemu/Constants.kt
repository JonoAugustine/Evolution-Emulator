package com.ampro.evemu

import com.ampro.Evolution.Dna.Codon
import com.ampro.evemu.dna.CODON_LENGTH
import com.ampro.evemu.dna.DNA
import java.util.*

/** A pair of values */
data class Pair<L, R>(var left: L? = null, var right: R? = null)

data class Range<L, R>(var min: L? = null, var max: R? = null)

///-------------------------------General------------------------
/** The standard US alphabet in upper case  */
val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
/** "arabic" numerals 0 to 9  */
val NUMERALS = "0123456789"


//--------------------CODON--------------

var allPossibleCodons = ArrayList<Codon>()
/**The minimum number of codons any gene can be */
var SCORE_RANGE = Range<Int, Int>() // -100 - 100?

var START_CODONS: Int = 0
var STOP_CODONS: Int = 0


//-----GENE--------------
var MINIMUM_GENE_LENGTH: Int = 0



//-----Trait-------------



//---------------------Chromatid---------
var CHROMATID_LENGTH_RANGE = Range<Int, Int>()
//TODO $Make it so each organism has an array of chromatid length ranges

//-----Chromosome--------
var CHROMATID_per_CHROMOSOME = 2
//TODO $Come up with a way to use odd numbers
//->(Problem: Which parent to take which chromatid from???)

var NUMBER_STARTING_DEFAULT_CHROMOSOME: Int = 0


//-----------------DNA Reader--------


///---------------Organism---------------------

/** Length of all Organism object DNA Sequences */
var DNA_LENGTH = 60

//-----------------------------Naming Related Variables--------
/**The current highest first index letter for Organism name generation */
var NEXT_NAME_LETTER_INDEX = 0
var NEXT_NAME__INDEX = 0
/**The current highest number for Organism name generation */
var NEXT_NAME_NUMBER = 0

//--------------------------------Population-----------
/**The current highest first index letter for Population name generation */
var P_NEXT_NAME_LETTER_INDEX = 0
var P_NEXT_NAME__INDEX = 0
/**The current highest number for Population name generation */
var P_NEXT_NAME_NUMBER = 0

private var setUp = false


/** Sets up all constants */
fun setUp() {
    val s: Scanner
    if (!setUp) {
        s = Scanner(System.`in`)

        println("Default? (true/1)>>")
        if (true) {//s.nextInt() == 1){

            println("Generating possible Codons")
            CODON_LENGTH = 3
            allPossibleCodonsGenerator(DNA, CODON_LENGTH)

            println(allPossibleCodons)

            println("Generating Start Codon")
            START_CODONS = 1
            for (i in 0 until START_CODONS) {
                val temp = Random().nextInt(allPossibleCodons.size)
                if (!allPossibleCodons[temp].isStart && !allPossibleCodons[temp].isStop)
                    allPossibleCodons[temp].setFunction(1)
                println(allPossibleCodons[temp])
            }

            println("Generating Stop Codons")
            STOP_CODONS = 3
            for (i in 0 until STOP_CODONS) {
                val temp = Random().nextInt(allPossibleCodons.size)
                if (!allPossibleCodons[temp].isStart && !allPossibleCodons[temp].isStop)
                    allPossibleCodons[temp].setFunction(-1)
                println(allPossibleCodons[temp])
            }

            NUMBER_STARTING_DEFAULT_CHROMOSOME = 3
            println("NUMBER_STARTING_DEFAULT_CHROMOSOME>> $NUMBER_STARTING_DEFAULT_CHROMOSOME")

            MINIMUM_GENE_LENGTH = 10
            println("Minimum Gene Length (in codons)>> $MINIMUM_GENE_LENGTH")

            SCORE_RANGE.min = -10
            println("Codon Score Minimum>> $SCORE_RANGE.min")

            SCORE_RANGE.max = 10
            println("Codon Score Minimum>> $SCORE_RANGE.max")

            CHROMATID_LENGTH_RANGE.min = 100
            println("Chromatid minimum length (in codons)>> $CHROMATID_LENGTH_RANGE.min")

            CHROMATID_LENGTH_RANGE.max = 200
            println("Chromatid Maximum length (in codons)>> $CHROMATID_LENGTH_RANGE.max")

            CHROMATID_per_CHROMOSOME = 2
            println("Number of Chromatids per Chromosome>> $CHROMATID_per_CHROMOSOME")

            /*
				System.out.println("Input Organism DNA Length (in codons)>>");
				DNA_LENGTH = s.nextInt();
				 */

            //System.out.println("Input >>");

            setUp = true
            s.close()
            println("Set up Done\n")
        } else {
            println("Input Starting Codon Length>>")
            CODON_LENGTH = s.nextInt()
            allPossibleCodonsGenerator(DNA, CODON_LENGTH)

            println("Input Number of Start Codons>>")
            START_CODONS = s.nextInt()
            for (i in 0 until START_CODONS) {
                val temp = Random().nextInt(allPossibleCodons.size)
                if (!allPossibleCodons[temp].isStart && !allPossibleCodons[temp].isStop)
                    allPossibleCodons[temp].setFunction(1)
            }

            println("Input Number of Stop Codons>>")
            STOP_CODONS = s.nextInt()
            for (i in 0 until START_CODONS) {
                val temp = Random().nextInt(allPossibleCodons.size)
                if (!allPossibleCodons[temp].isStart && !allPossibleCodons[temp].isStop)
                    allPossibleCodons[temp].setFunction(-1)
            }

            println("Input Starting Default Organism Number of Chromosomes>>")
            NUMBER_STARTING_DEFAULT_CHROMOSOME = s.nextInt()

            println("Input Minimum Gene Length (in codons)>>")
            MINIMUM_GENE_LENGTH = s.nextInt()

            println("Input Codon Score Minimum>>")
            SCORE_RANGE.min = s.nextInt()

            println("Input Codon Score Maximum>>")
            SCORE_RANGE.max = s.nextInt()

            println("Input Chromatid minimum length (in codons)>>")
            CHROMATID_LENGTH_RANGE.min = s.nextInt()

            println("Input Chromatid maximum length (in codons)>>")
            CHROMATID_LENGTH_RANGE.max = s.nextInt()

            println("Input Number of Chromatids per Chromosome>>")
            CHROMATID_per_CHROMOSOME = s.nextInt()

            println("Input Organism DNA Length (in codons)>>")
            DNA_LENGTH = s.nextInt()

            //System.out.println("Input >>");

            setUp = true
            s.close()
            println("Set up Done\n")
        }
    }

}

/**
 *
 * @param arr
 * @param k
 */
fun allPossibleCodonsGenerator(arr: Array<DNA>, k: Int) {
    val n = arr.size
    val idx = IntArray(k)
    val perm = arrayOfNulls<String>(k)
    while (true) {
        for (i in 0 until k)
            perm[i] = arr[idx[i]]
        allPossibleCodons.add(Codon(perm.joinToString("")))
        // generate the next permutation
        var i = idx.size - 1
        while (i >= 0) {
            idx[i]++
            if (idx[i] < n)
                break
            idx[i] = 0
            i--
        }
        // if the first index wrapped around then we're done
        if (i < 0)
            break
    }
}
