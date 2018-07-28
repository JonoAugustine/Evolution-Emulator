package com.ampro.evemu.util

import com.ampro.evemu.constants.ALPHABET

/** An object to name things sequentially */
class SequentialNamer(val prefix: String = "", val maxInt: Int = 99_000,
                      letterLength: Int = 2) {

    private val let = permute(ALPHABET.toCharArray().toTypedArray(), letterLength)
    private val letters = Array(let.size) { index -> let[index].joinToString(separator = "") }

    var nextLetterIndex : Int = 0
    var nextNumber : Int = 0

    @Throws(NoSuchElementException::class)
    fun next(secondPrefix: Any = "") : String {
        val sb = StringBuilder("${prefix}_${secondPrefix}_")
        return synchronized(this) {
            if (nextNumber !in 0..maxInt) {
                nextNumber = 0
                if (++nextLetterIndex !in 0 until letters.size) {
                    throw NoSuchElementException("Exhausted Letter Names")
                }
            }
            sb.append(letters[nextLetterIndex])//.append("_")
                .append(nextNumber++)
            sb.toString()
        }
    }

    fun reset() {
        nextLetterIndex = 0
        nextNumber = 0
    }
}
