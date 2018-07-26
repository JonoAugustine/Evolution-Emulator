package com.ampro.evemu.constants

import java.io.File

/** A pair of values */
data class Pair<L, R>(var left: L? = null, var right: R? = null)

/** An inclusive range of values */
abstract class Range<V>(var min: V, var max: V) {
    /** @return true if the given value is within the range */
    abstract fun includes(x: V) : Boolean
    override fun toString(): String = "$min, $max"
}

class IntRange(min: Int, max: Int) : Range<Int>(min, max) {
    override fun includes(x: Int): Boolean = x in (min)..(max)
}

class FloatRange(min: Float, max: Float) : Range<Float>(min, max) {
    override fun includes(x: Float): Boolean = x in (min)..(max)
}

/////////////// FILES \\\\\\\\\\\\\\

val DIR_ROOT  = File("evemu")
val DIR_CONST = File(DIR_ROOT, "constants")

