package com.ampro.evemu.util

import com.ampro.evemu.ribonucleic.DNA
import com.ampro.evemu.ribonucleic.RNA
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun <E> ArrayList<E>.addAll(arr: Array<E>) = arr.forEach { this.add(it) }
fun <E> ArrayList<E>.addAll(insert: Int, arr: Array<E>) {
    if (this.size - insert < arr.size)
        throw IndexOutOfBoundsException("""
            ArrayList Size: ${this.size}
            Insert index: $insert
            Insert size: ${arr.size}
            """.trimIndent()
        )
    var idx = insert
    arr.forEach { this.add(idx++, it) }
}

/** @return The current local date and time. dd-MM-yyyy HH:mm:ss */
val NOW: String get() = LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))

/**
 * A Timer object contains a start-time (long) that is defined upon creation.<br></br>
 * The method getTime() returns the current duration of the timer's run time in seconds.
 *
 * @author Jonathan Augustine
 */
class Timer(private var startTime: Long = System.currentTimeMillis()) {

    private var running: Boolean = true

    private var elapsedTime: Long = -1

    fun getElapsedTime() : Long {
        return if (!running) {
            elapsedTime
        } else {
            System.currentTimeMillis() - startTime
        }
    }

    /** @return The current duration of the timer's run time in HH:MMM:SS */
    fun formattedTime(): String = format(this.getElapsedTime())

    companion object {
        fun format(millis: Long) : String {
            var seconds = (millis / 1_000).toDouble()//Math.pow(10.0, 9.0)
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
    }

    /** @return The current duration of the timer's run time in HH:MMM:SS */
    override fun toString(): String = this.formattedTime()

    /**
     * Resets the starting time and returns the previous duration
     *
     * @return the last duration formatted
     */
    fun reset() : String {
        val lastTime = this.formattedTime()
        this.startTime = System.currentTimeMillis()
        return lastTime
    }

    /**
     * Stop the timer (set the end time)
     *
     * @return The formatted duration
     */
    fun stop() : String {
        this.elapsedTime = System.currentTimeMillis()
        this.running = false
        return this.formattedTime()
    }

}

/**
 * @param timers Array of Timers
 * @return The average duration of the provided Timers, formatted HH:MMM:SS
 */
fun timerAvg(timers: Array<Timer>): String {

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

fun Array<DNA>.stringify() : String {
    var s = ""
    this.forEach { s += it.name }
    return s
}

fun Array<RNA>.stringify() : String {
    var s = ""
    this.forEach { s += it.name }
    return s
}
