package com.ampro.evemu.constants

/** The standard US alphabet in upper case  */
enum class Alphabet {
    A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z;
    companion object { const val size = 26 }
}

fun List<Alphabet>.stringify() : String {
    val sb = StringBuilder()
    this.forEach { sb.append(it) }
    return sb.toString()
}

fun Array<Alphabet>.stringify() : String {
    val sb = StringBuilder()
    this.forEach { sb.append(it) }
    return sb.toString()
}
