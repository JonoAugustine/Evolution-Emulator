package com.ampro.evemu

import com.ampro.evemu.constants.BioConstants
import com.ampro.evemu.constants.DIR_CONST
import com.ampro.evemu.constants.DIR_ROOT
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.runBlocking
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*


val scan = Scanner(System.`in`)

var BIO_CONSTANTS: BioConstants = loadOrBuild()

fun main(args: Array<String>) {
    //Build environments

    //Start emulator coroutines
}

fun loadOrBuild() : BioConstants {
    val gson = GsonBuilder().setPrettyPrinting().create()
    buildDirs()
    var set: Boolean
    println("Load previous BioConstants? (y/n)")
    if (scan.nextLine().equals("y", true)) {
        println("BioConstants JSON file name: ")
        val filename = scan.nextLine()
        val file = File(DIR_CONST, "$filename.json")
        if (file.exists()) {
            try {
                val reader = FileReader(file)
                BIO_CONSTANTS = gson.fromJson(reader, BioConstants::class.java)
                set = true
            } catch (e: Exception) {
                System.err.println("Failed to load file '$filename'.\n${e.cause}")
                set = false
            }
        } else {
            System.err.println("Failed to load file '$filename'.\nFile not found")
            set = false
        }
    } else {
        set = false
    }
    return if (!set) {
        val build = runBlocking { BioConstants.build() }
        println("Would you like to save these settings? (y,n)")
        scan.reset()
        if (scan.nextLine().equals("y", true)) {
            try {
                val writer = FileWriter(File(DIR_CONST, "${build.name}.json"))
                gson.toJson(build, writer)
                writer.close()
            } catch (e: Exception) {
                System.err.println("Save Failed! :\n${e.cause}")
            }
        }
        scan.close()
        build
    } else BIO_CONSTANTS
}

fun buildDirs() {
    if (!DIR_ROOT.exists())
        FileUtils.forceMkdir(DIR_ROOT)
    if (!DIR_CONST.exists())
        FileUtils.forceMkdir(DIR_CONST)
}
