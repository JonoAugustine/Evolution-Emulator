package com.ampro.evemu.util

import com.ampro.evemu.constants.Alphabet

/** An object to name things sequentially */
class SequentialNamer(val prefix: List<Alphabet>, val maxInt: Int = 99_000,
                      val letterLength: Int = 2) {

    var nextLetterIndex : Int = 0
    var nextNumber : Int = 0

    var idx = IntArray(letterLength)

    @Throws(NoSuchElementException::class)
    fun next(vararg secondPrefix: Any) : String {
        val sb = StringBuilder()
        if (prefix.isNotEmpty()) {
            sb.append("${prefix.joinToString("")}_")
        }

        if (secondPrefix.isNotEmpty()) {
            sb.append(secondPrefix.joinToString("")).append("_")
        }

        val perm = ArrayList<Alphabet>(letterLength)
        return synchronized(this) {
            //Build the next letter sequence
            for (k in 0 until letterLength) {
                perm.add(k, Alphabet.values()[idx[k]])
            }

            //If we reach the end of the numbers
            if (nextNumber !in 0..maxInt) {
                // generate the next permutation
                var i = idx.size - 1
                while (i >= 0) {
                    if (++idx[i] < Alphabet.size) break
                    idx[i--] = 0
                }
                // if the first index wrapped around then we're done
                if (i < 0) {
                    throw NoSuchElementException("Exhausted Letter Names")
                }
                nextNumber = 0
            }
            sb.append(perm.joinToString("")).append(nextNumber++)
            sb.toString()
        }
    }

    fun reset() {
        nextLetterIndex = 0
        nextNumber = 0
    }
}
