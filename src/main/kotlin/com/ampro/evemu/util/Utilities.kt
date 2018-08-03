package com.ampro.evemu.util

import com.ampro.evemu.ribonucleic.DNA
import com.ampro.evemu.ribonucleic.RNA
import java.util.*

fun <E> ArrayList<E>.addAll(arr: Array<E>) = arr.forEach { this.add(it) }
fun <E> ArrayList<E>.addAll(insert: Int, arr: Array<E>) {
    if (insert !in 0 until arr.size) {
        throw IndexOutOfBoundsException("""
            ArrayList Size: ${this.size}
            Insert index: $insert
            Insert size: ${arr.size}
            """.trimIndent())
    }
    var idx = insert
    arr.forEach { this.add(idx++, it) }
}

/** @return Each base in one long string. ACTGTCATG */
fun Array<DNA>.stringify() : String {
    var s = ""
    this.forEach { s += it.name }
    return s
}
/** @return Each base in one long string. ACUGUCATG */
fun Array<RNA>.stringify() : String {
    var s = ""
    this.forEach { s += it.name }
    return s
}
