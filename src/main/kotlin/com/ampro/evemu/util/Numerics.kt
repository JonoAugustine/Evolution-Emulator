package com.ampro.evemu.util

import java.math.BigInteger
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger

fun random(min: Int = 0, max: Int) : Int = if (min >= max) { 0 }
else { ThreadLocalRandom.current().nextInt(min, max + 1) }

operator fun AtomicInteger.minus(other: AtomicInteger) = this.get() - other.get()

inline fun <reified T> permute(src: Array<T>, size: Int = src.size) : List<List<T>> {
    if (src.isEmpty()) return listOf()
    val n = src.size
    val idx = IntArray(size)
    val perm = arrayOfNulls<T>(size)
    val out = ArrayList<List<T>>()
    while (true) {
        for (k in 0 until size) perm[k] = src[idx[k]]
        out.add(perm.toList() as List<T>)
        // generate the next permutation
        var i = idx.size - 1
        while (i >= 0) {
            if (++idx[i] < n) break
            idx[i--] = 0
        }
        // if the first index wrapped around then we're done
        if (i < 0)
            break
    }
    return out
}

/**
 * Returns the number of permutations of the pool of size "size"
 *
 * @param size
 * @param pool
 * @return number of permutations
 */
fun permuteSize(pool: Int, size: Int): Int {
    if (size == 0) return 0
    return try {
        factorial(pool).divide(factorial(pool - size)).toInt()
    } catch (e: ArithmeticException) {
        0
    }
}

fun factorial(i: Int): BigInteger {
    var retu = BigInteger.valueOf(i.toLong())
    for (k in 1..i) {
        retu = retu.multiply(BigInteger.valueOf(k.toLong()))
    }
    return retu
}

/**
 * Removes all letters from the input String and returns the resulting number sequence
 * @param input String
 * @return int
 */
fun removeLetters(input: String): Int = try {
    Integer.parseInt(input.replace("[^\\d]", ""))
} catch (e: NumberFormatException) {
    -1
}

/**
 * Takes a string and returns true if the string is a digit
 * @param input
 * @return boolean
 */
private fun isInteger(input: String): Boolean = try {
    Integer.parseInt(input)
    true
} catch (e: Exception) {
    false
}
