package com.ampro.evemu.organism

import com.ampro.evemu.organism.ReproductiveType.*
import com.ampro.evemu.util.SequentialNamer

//|||||||||||||||-Naming Related Variables-||||||||||||||||\
/**The current highest first index letter for Organism name generation */
var O_NEXT_NAME_LETTER_INDEX = 0
var O_NEXT_NAME__INDEX = 0
/**The current highest number for Organism name generation */
var O_NEXT_NAME_NUMBER = 0

class Organism(val name: String = organismNamer.next(),
               val parents: Array<Organism?>,
               val reproductiveType: ReproductiveType = CLONE) {
    var alive: Boolean = true
    var age: Int = 0

    var fitness: Float = 0f


}

val organismNamer: SequentialNamer = SequentialNamer()
