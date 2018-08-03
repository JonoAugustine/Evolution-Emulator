package com.ampro.evemu

import com.ampro.evemu.constants.BioConstants
import com.ampro.evemu.emulation.SimpleEmulator
import com.ampro.evemu.emulation.SimpleEnvironment
import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.Population
import com.ampro.evemu.organism.ReproductiveType
import com.ampro.evemu.organism.ReproductiveType.CLONE
import com.ampro.evemu.organism.ReproductiveType.SEX
import com.ampro.evemu.organism.SimpleOrganism
import com.ampro.evemu.util.Slogger
import com.ampro.evemu.util.Timer
import com.ampro.evemu.util.elog
import com.ampro.evemu.util.io.DIR_CONST
import com.ampro.evemu.util.io.DIR_ENVIR
import com.ampro.evemu.util.io.DIR_LOGS
import com.ampro.evemu.util.io.DIR_ROOT
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
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis


val scan = Scanner(System.`in`)
var BIO_C: BioConstants = loadOrBuild()

val CACHED_POOL =  Executors.newCachedThreadPool().asCoroutineDispatcher()
var FIXED_POOL = newFixedThreadPoolContext(7_000, "FixedPool")

fun main(args: Array<String>) = runBlocking {

    slog("Building populations...")
    val pList = ArrayList<Population<out Organism>>(5)
    val time = measureTimeMillis {
        for (i in 1..8) {
            slog("$i time=" + measureTimeMillis {
                pList.add(Population(population = genTestOrgs(10_000, SEX)))
            } / 1_000.0)
        }
    }
    slog("...done (time=${Timer.format(time)})\n")

    val emu = SimpleEmulator(environment = SimpleEnvironment(pList), years = 1000)

    emu.run()

    FIXED_POOL.close()
    CACHED_POOL.close()
}

val testSlogger = Slogger("genTestOrgs")
fun genTestOrgs(size: Int = 1_000, type: ReproductiveType = CLONE)
        : ArrayList<SimpleOrganism> {
    val out = ArrayList<SimpleOrganism>(size)
    (0 until size).forEach { out.add(SimpleOrganism(reproductiveType = type)) }
    return out
}


fun test(testSize: Int = 10_000, debug: Boolean = true): ArrayList<Organism> {
    val prodMap = ConcurrentHashMap<String, AtomicInteger>()/*(mapOf(
            "FixedPool-1" to AtomicInteger(), "FixedPool-2" to AtomicInteger(),
            "FixedPool-3" to AtomicInteger(), "FixedPool-4" to AtomicInteger(),
            "FixedPool-5" to AtomicInteger(), "FixedPool-6" to AtomicInteger(),
            "FixedPool-7" to AtomicInteger(), "FixedPool-8" to AtomicInteger(),
            "FixedPool-9" to AtomicInteger(), "FixedPool-10" to AtomicInteger(),
            "FixedPool-11" to AtomicInteger(), "FixedPool-12" to AtomicInteger(),
            "FixedPool-13" to AtomicInteger(), "FixedPool-14" to AtomicInteger(),
            "FixedPool-15" to AtomicInteger(), "FixedPool-16" to AtomicInteger()
    ))*/
    val out = ArrayList<Organism>(testSize)
    val time = measureTimeMillis {
        val jobs = List(testSize) { _ ->
            // launch a lot of coroutines and list their jobs
            async (FIXED_POOL) {
                if (debug) {
                    prodMap.putIfAbsent(Thread.currentThread().name, AtomicInteger(1))
                        ?.incrementAndGet()
                }
                SimpleOrganism(reproductiveType = SEX)
            }
        }
        runBlocking { out.addAll(jobs.awaitAll()) }
    }
    slog("${time / 1_000} sec")
    if (debug) {
        slog(prodMap)
        var min: Int = testSize
        var max: Int = 0
        slog("Max-Min dif=" + prodMap.let {
            it.forEach {
                val value = it.value.toInt()
                if (value < min) min = value
                else if (value > max) max = value
            }
            max - min
        } + "\tDif % of max=${((max - min).toDouble() / testSize.toDouble()) * 100}\n")
    }
    return out
}

fun loadOrBuild() : BioConstants {
    val gson = GsonBuilder().setPrettyPrinting().create()
    buildDirs()
    var set: Boolean
    slog("Load previous BioConstants? (y/n): ")
    if (scan.nextLine().equals("y", true)) {
        slog("BioConstants JSON file name: ")
        val filename = scan.nextLine()
        val file = File(DIR_CONST, "$filename.json")
        if (file.exists()) {
            try {
                val reader = FileReader(file)
                BIO_C = gson.fromJson(reader, BioConstants::class.java)
                set = true
            } catch (e: Exception) {
                elog("Failed to load file '$filename'.\n${e.cause}")
                set = false
            }
        } else {
            elog("Failed to load file '$filename'.\nFile not found")
            set = false
        }
    } else {
        set = false
    }
    return if (!set) {
        val build = runBlocking { BioConstants.build() }
        slog("Would you like to save these settings? (y,n)")
        scan.reset()
        if (scan.nextLine().equals("y", true)) {
            try {
                val writer = FileWriter(File(DIR_CONST, "${build.name}.json"))
                gson.toJson(build, writer)
                writer.close()
            } catch (e: Exception) {
                elog("Save Failed! :\n${e.cause}")
            }
        }
        scan.close()
        build
    } else BIO_C
}

fun buildDirs() {
    elog("Building Directories...")
    if (!DIR_ROOT.exists())
        FileUtils.forceMkdir(DIR_ROOT)
    if (!DIR_CONST.exists())
        FileUtils.forceMkdir(DIR_CONST)
    if (!DIR_ENVIR.exists())
        FileUtils.forceMkdir(DIR_ENVIR)
    if (!DIR_LOGS.exists())
        FileUtils.forceMkdir(DIR_LOGS)
    elog("Directories Built")
}
