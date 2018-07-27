package com.ampro.evemu.util

import java.util.concurrent.ThreadLocalRandom
import kotlin.ranges.IntRange

/** A pair of values */
data class Pair<L, R>(var left: L? = null, var right: R? = null)

/** An inclusive range of values */
abstract class Range<V>(var min: V, var max: V) {
    /** @return true if the given value is within the range */
    abstract fun includes(x: V) : Boolean
    /** @return A random value within the range */
    abstract fun random() : V
    override fun toString(): String = "$min, $max"
}

class IntRange(min: Int, max: Int) : Range<Int>(min, max) {
    override fun random(): Int = ThreadLocalRandom.current().nextInt(min, max + 1)
    override fun includes(x: Int): Boolean = x in (min)..(max)
}

class FloatRange(min: Float, max: Float) : Range<Float>(min, max) {
    override fun random(): Float
            = ThreadLocalRandom.current().nextFloat() * (max - min) + min
    override fun includes(x: Float): Boolean = x in (min)..(max)
}

class DoubleRange(min: Double, max: Double) : Range<Double>(min, max) {
    override fun includes(x: Double): Boolean = x in (min)..(max)
    override fun random(): Double = ThreadLocalRandom.current().nextDouble()
}
