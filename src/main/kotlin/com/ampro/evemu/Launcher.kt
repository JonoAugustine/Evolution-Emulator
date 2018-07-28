package com.ampro.evemu

import com.ampro.evemu.constants.BioConstants
import com.ampro.evemu.constants.DIR_CONST
import com.ampro.evemu.constants.DIR_ROOT
import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.SimpleOrganism
import com.ampro.evemu.util.elog
import com.ampro.evemu.util.slog
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis


val scan = Scanner(System.`in`)
var FIXED_POOL = newFixedThreadPoolContext(16, "FixedPool")
var BIO_CONSTANTS: BioConstants = loadOrBuild()

val CACHED_POOL =  Executors.newCachedThreadPool().asCoroutineDispatcher()

fun main(args: Array<String>) = runBlocking {
    //Build environments
    //Start emulator coroutines
    //Fixedy stuff
    test()





    FIXED_POOL.close()
}

internal suspend fun test(testSize: Int = 10_000) {
    val prodMap = ConcurrentHashMap<String, AtomicInteger>(mapOf(
            "FixedPool-1" to AtomicInteger(), "FixedPool-2" to AtomicInteger(),
            "FixedPool-3" to AtomicInteger(), "FixedPool-4" to AtomicInteger(),
            "FixedPool-5" to AtomicInteger(), "FixedPool-6" to AtomicInteger(),
            "FixedPool-7" to AtomicInteger(), "FixedPool-8" to AtomicInteger(),
            "FixedPool-9" to AtomicInteger(), "FixedPool-10" to AtomicInteger(),
            "FixedPool-11" to AtomicInteger(), "FixedPool-12" to AtomicInteger(),
            "FixedPool-13" to AtomicInteger(), "FixedPool-14" to AtomicInteger(),
            "FixedPool-15" to AtomicInteger(), "FixedPool-16" to AtomicInteger()
    ))
    val arr = arrayOfNulls<Organism>(testSize)
    val time = measureTimeMillis {
        val jobs = List(testSize) {index: Int ->
            // launch a lot of coroutines and list their jobs
            async (FIXED_POOL) {
                prodMap[Thread.currentThread().name]?.incrementAndGet()
                arr[index] = SimpleOrganism()
            }
        }
        runBlocking { jobs.awaitAll() }
    }
    arr.forEach { slog(it?: "null") }
    println("${time / 1000} sec")
    println(prodMap.toSortedMap(kotlin.Comparator { n, n2 ->
        prodMap[n2]!!.toInt() - prodMap[n]!!.toInt()
    }).toString())
    var min: Int = testSize
    var max: Int = 0
    println("Max-Min dif=" + prodMap.let {
        it.forEach { val value = it.value.toInt()
            if (value < min) min = value
            else if (value > max) max = value
        }
        max - min
    }
            + "\nDif % of max=${((max-min).toDouble()/testSize.toDouble()) * 100}")
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
