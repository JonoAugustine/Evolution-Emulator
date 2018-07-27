package com.ampro.evemu.util

class SequentialNamer(val prefix: String = "") {

    /**The current highest first index letter for Organism name generation */
    var nextNameLetterIndex = 0
    var nextNameIndex = 0
    /**The current highest number for Organism name generation */
    var nextNameNumber = 0

    /**
     * @return The next formatted name in the sequence. A_0
     */
    fun next() : String {
        var name = ""
        if (this.generation != null)
            name += this.generation + "-"
        try {
            name += ALPHABET.substring(NEXT_NAME_LETTER_INDEX, NEXT_NAME_LETTER_INDEX + 1)
        } catch (e: IndexOutOfBoundsException) {
            name += ALPHABET.substring(NEXT_NAME_LETTER_INDEX)
            NEXT_NAME_LETTER_INDEX = 0
            try {
                name += "_" + ALPHABET.substring(NEXT_NAME_LETTER_INDEX, NEXT_NAME_LETTER_INDEX + 1)
            } catch (e2: IndexOutOfBoundsException) {
                name += "_" + ALPHABET.substring(NEXT_NAME_LETTER_INDEX)
                NEXT_NAME_LETTER_INDEX = 0
                NEXT_NAME_NUMBER = 0
            }

        }


        name += "_"
        for (i in 5 downTo String("" + NEXT_NAME_NUMBER + "").length + 1)
            name += "0"
        name += NEXT_NAME_NUMBER++
        if (NEXT_NAME_NUMBER === 99999) {
            NEXT_NAME_NUMBER = 0
            NEXT_NAME_LETTER_INDEX++
        }

        return name
    }
}
