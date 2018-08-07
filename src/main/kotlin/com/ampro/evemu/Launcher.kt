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
import com.ampro.evemu.util.elog
import com.ampro.evemu.util.io.*
import com.ampro.evemu.util.slog
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


val CACHED_POOL =  Executors.newCachedThreadPool().asCoroutineDispatcher()
var FIXED_POOL = newFixedThreadPoolContext(7_000, "FixedPool")

val scan = Scanner(System.`in`)
var BIO_C: BioConstants = loadOrBuild()

fun main(args: Array<String>) = runBlocking {

    val emulators = 1
    val pops = 5
    val popSize = 10_000

    val emus = List (emulators) {
        SimpleEmulator(environment = genTestEnv(numPop = pops, popSize = popSize)[0])
    }

    val emuJobs = List (emulators) {
        launch (FIXED_POOL) { emus[it].run() }
    }

    emuJobs.joinAll()

    FIXED_POOL.close()
    CACHED_POOL.close()
}

fun genTestEnv(numEnv: Int = 1, numPop: Int = 1, popSize: Int = 1_000)
        : List<SimpleEnvironment> =
    List (numEnv) {
        val list = ArrayList<Population<out Organism>>(List(numPop) {
            Population(population = genTestOrgs(popSize, SEX))
        })
        SimpleEnvironment(populations = list)
    }

val testSlogger = Slogger("genTestOrgs")
fun genTestOrgs(size: Int = 1_000, type: ReproductiveType = CLONE)
        : ArrayList<SimpleOrganism> {
    val out = ArrayList<SimpleOrganism>(size)
    (0 until size).forEach { out.add(SimpleOrganism(reproductiveType = type)) }
    return out
}

fun test(testSize: Int = 10_000, debug: Boolean = true): ArrayList<Organism> {
    val prodMap = ConcurrentHashMap<String, AtomicInteger>()
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
        var max = 0
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
                BIO_C = GSON.fromJson(reader, BioConstants::class.java)
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
                GSON.toJson(build, writer)
                writer.close()
                slog("Saved")
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
