package com.ampro.evemu.util

import java.math.BigInteger
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger

fun random(min: Int = 0, max: Int) : Int = if (min >= max) { 0 }
else { ThreadLocalRandom.current().nextInt(min, max + 1) }

/** @return (this / of) * 100 */
fun Number.percent(of: Number) = (this.toDouble() / of.toDouble()) * 100

/** @return The number squared */
fun Number.sqr() = this.toDouble() * this.toDouble()

 //                        \\
// AtomicInteger extensions \\

operator fun AtomicInteger.unaryMinus() = -this.get()
operator fun AtomicInteger.minus(other: AtomicInteger) = getAndAdd(-other)
operator fun AtomicInteger.plus(other: AtomicInteger)  = getAndAdd(other.get())

/**
 * @param src The source list of things to permute
 * @param size The size of each permutation
 * @return All possible permutations of the src array
 */
inline fun <reified T> permute(src: Array<T>, size: Int = src.size) : List<List<T>> {
    if (src.isEmpty()) return listOf()
    val n = src.size
    val idx = IntArray(size)
    val perm = ArrayList<T>(size)
    val out = ArrayList<List<T>>()
    while (true) {
        for (k in 0 until size) {
            perm.add(k, src[idx[k]])
        }
        out.add(perm.toList())
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
 * Returns the number of permutations of the src of size "size"
 *
 * @param size
 * @param src
 * @return number of permutations
 */
fun permuteSize(src: Int, size: Int): Int {
    if (size == 0) return 0
    return try {
        factorial(src).divide(factorial(src - size)).toInt()
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
