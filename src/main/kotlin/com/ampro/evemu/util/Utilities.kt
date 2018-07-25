package com.ampro.evemu.util

import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*


/**
 * A Timer object contains a start-time (long) that is defined upon creation.<br></br>
 * The method getTime() returns the current duration of the timer's run time in seconds.
 *
 * @author Jonathan Augustine
 */
class Timer(private var startTime: Long = System.nanoTime()) {

    var stopped: Boolean = false

    private var elapsedTime: Long = -1

    fun getElapsedTime() : Long {
        return if (stopped) {
            elapsedTime
        } else {
            System.nanoTime() - startTime
        }
    }

    /** @return The current duration of the timer's run time in HH:MMM:SS */
    fun formattedTime(): String {
        var seconds = this.elapsedTime / Math.pow(10.0, 9.0)
        var hours = 0
        var min = 0
        if (seconds > 60) {
            var i = 0
            while (i < seconds) {
                if (i == 60) {
                    min++
                    seconds -= 60.0
                    i = 0
                    if (min == 60) {
                        hours++
                        min -= 60
                    }
                }
                i++
            }
        }
        return "$hours:$min:$seconds"
    }

    /** @return The current duration of the timer's run time in HH:MMM:SS */
    override fun toString(): String = this.formattedTime()

    /**
     * Resets the starting time and returns the previous duration
     * @return the last duration formatted
     */
    fun reset() : String {
        val lastTime = this.formattedTime()
        this.startTime = System.nanoTime()
        return lastTime
    }

    /**
     * Stop the timer (set the end time)
     * @return The formatted duration
     */
    fun stop() : String {
        this.elapsedTime = System.nanoTime()
        this.stopped = true
        return this.formattedTime()
    }

}

/**
 * @param timers Array of Timers
 * @return The average duration of the provided Timers, formatted HH:MMM:SS
 */
fun timerAverage(timers: Array<Timer>): String {

    var AverageNanoTime: Long = 0

    timers.forEach { AverageNanoTime += it.getElapsedTime() }

    AverageNanoTime /= timers.size.toLong()

    var seconds = AverageNanoTime / Math.pow(10.0, 9.0)
    var hours = 0
    var min = 0
    if (seconds > 60) {
        var i = 0
        while (i < seconds) {
            if (i == 60) {
                min++
                seconds -= 60.0
                i = 0
                if (min == 60) {
                    hours++
                    min -= 60
                }
            }
            i++
        }
    }
    return "$hours:$min:$seconds"
}

/**
 * Returns the number of permutations of the pool of size "size"
 * @param size
 * @param pool
 * @return number of permutations
 */
fun permute(pool: Int, size: Int): Int {
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

fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().time)
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