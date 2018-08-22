package com.ampro.evemu.emulation

import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.constants.Alphabet.*
import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.Population
import com.ampro.evemu.organism.ReproductiveType.SEX
import com.ampro.evemu.ribonucleic.READ_POOL
import com.ampro.evemu.ribonucleic.score
import com.ampro.evemu.util.*
import kotlinx.coroutines.experimental.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

internal val envNamer = SequentialNamer(listOf(E,N,V), 100, 2)

internal const val CYCLEDURATIONS = "Cycle Durations"
internal const val FILIALGENTIME  = "Filial Generation Time"
internal const val AVERAGINGTIME  = "Averaging Time"
internal const val SCORINGTIME    = "Scoring Time"
internal const val CULLTIME       = "Cull Time"
internal const val CULLCUTOFF     = "Cull Cutoff"
internal const val ORGCULLED      = "Orgs Culled"
internal const val AF_INIT        = "Pre-Mating"
internal const val AF_POST_M      = "Post-Mating"
internal const val AF_POST_C      = "Post-Culling"
internal const val BESTORGCHANGE  = "Best Organism Change"

/**
 * A Simple, straight-forward emulator for testing and learning.
 *
 * @param name The name of the emulator, defaults to an auto-generated
 *          sequential name
 * @param environment The [Environment] to run the Emulation with
 * @param years The number of "year" or "cycles" to run the [SimpleEmulator]
 *
 * @author Jonathan Augustine
 * @since 3.0
 */
class SimpleEmulator(val name: String = envNamer.next(),
                     val environment: SimpleEnvironment,
                     var years: Int = 100) : Runnable {

    inner class PopStat(name: String) {
        val CYCLEDURATIONS = ArrayList<Long>(years * environment.size)
        val FILIALGENTIME = ArrayList<Long>(years)
        val AVERAGINGTIME = ArrayList<Long>(years)
        val SCORINGTIME = ArrayList<Long>(years)
        val CULLTIME = ArrayList<Long>(years)
        val CULLCUTOFF = ArrayList<Double>(years)
        val ORGCULLED = ArrayList<Int>(years)
        val AF_INIT = ArrayList<Double>(years)
        val AF_POST_M = ArrayList<Double>(years)
        val AF_POST_C = ArrayList<Double>(years)
        val BESTORGCHANGE = ArrayList<Int>(years)
    }

    /**
     * A collection of statistics from a [Population].
     *
     * @key The name of the [Population]
     * @value The HashMap of stats for the [Population]
     */
    val popStats = ConcurrentHashMap<String, PopStat>().apply {
        environment.populations.forEach { put(it.name, PopStat(it.name)) }
    }

    val yearDurations = ArrayList<Long>(years)

    private val log = InternalLog(name,
            years * environment.populations.size * 10, true, true)

    override fun run() {
        try {
            log.logAndPrint("Starting Emulator...")
            log.logAndPrint("Calculating Parent Fitness...")
            val startupTime = measureTimeMillis {
                runBlocking {
                    List(environment.size) { i ->
                        async(FIXED_POOL) {
                            measureTimeMillis { scorePopulation(environment[i]) }
                        }
                    }.awaitAll().forEach { long -> elog("time=${long.div(1_000)}") }
                }
            }
            log.logAndPrint("...DONE (time=${startupTime / 1_000})")
            environment.populations.forEach { log.logAndPrint(it) }

            val fullTimer = Timer()

            for (year in 0 until years) {
                val yearTime = measureTimeMillis {
                    runBlocking {
                        List(environment.populations.size) { pop ->
                            async(FIXED_POOL) {
                                yearCycle(environment.populations[pop], year)
                            }
                        }.awaitAll()
                    }
                }

                yearDurations.add(yearTime)
                log.logAndPrint("End Of Year Summery : ", true)
                log.logAndPrint("Year $year duration=${Timer.format(yearTime)}", true)
                popStats.apply {
                    val cycleDurations = ArrayList<Long>(years)
                    this.forEach { _, stats ->
                        cycleDurations.add(stats.CYCLEDURATIONS[year])
                    }
                    log.logAndPrint("Avg Cycle duration=${cycleDurations.let {
                        Timer.format(it.sum() / it.size) }}", true)
                }
                log.logAndPrint("Avg Year duration=${yearDurations.let {
                    Timer.format(it.sum() / it.size)
                }}", true)
                log.logAndPrint("Full Run Duration = $fullTimer", true)
                environment.populations.forEach { log.logAndPrint(it, true) }
                log.logAndPrint("Current Time<<<<>>>>$NOW\n\n", true)
            }
        } catch (e: Exception) {
            log.logAndPrint(e)
        }
        log.toFile()
    }

    /**
     *
     */
    private fun <O: Organism> yearCycle(pop: Population<O>, year: Int) = runBlocking {
        val tempLog = InternalLog("${pop.name} yearCycle", showName = true)
        val stats = popStats[pop.name]!!
        if (pop.isEmpty) {
            tempLog.log("Population ${pop.name} is empty.")
            stats.CYCLEDURATIONS.add(0L)
            return@runBlocking
        } else if (pop[0].reproductiveType == SEX && pop.size < 2) {
            pop.clear()
            tempLog.log("Population ${pop.name} died out.")
            stats.CYCLEDURATIONS.add(0L)
            return@runBlocking
        }

        val preMateAvg = pop.avgFitness

        var postMateAvg = 0.0

        val bestPreFOrg = async (FIXED_POOL) {
            val s = System.currentTimeMillis()
            val bestOrg = bestOrg(pop.population.toList())
            Pair(bestOrg, System.currentTimeMillis() - s)
        }

        val filial = Population<O>(pop.name, ArrayList(pop.size * 2))
        val reproTime = measureTimeMillis {
            filial.addAll(pop.reproduce(random(pop.size / 50, pop.size / 10),  5))
            pop.addAll(filial)
        }

        val avergingTime = measureTimeMillis { postMateAvg = pop.avgFitness }

        //Score the new filial generation
        val scoringTime = measureTimeMillis { if (filial.isEmpty) else scorePopulation(filial) }

        var cutoff: Double = 0.0
        var culled = 0
        val cullTime = measureTimeMillis {
            cutoff = postMateAvg - random(2, 4) * pop.stdDeviation
            val iterator = pop.population.listIterator()
            while (iterator.hasNext()) {
                val cur = iterator.next()
                if (cur.fitness < cutoff || cur.age >= 40 || cur.fitness <= 0) {
                    iterator.remove()
                    culled++
                } else {
                    cur.age++
                }
            }
        }

        val bestPostFOrg = if (pop.population.isNotEmpty()) {
            async (FIXED_POOL) {
                val s = System.currentTimeMillis()
                val bestOrg = bestOrg(pop.population.toList())
                Pair(bestOrg, System.currentTimeMillis() - s)
            }
        } else { async { bestPreFOrg.await().copy(right = -1L) } }

        val prePair  = bestPreFOrg.await()
        val postPair = bestPostFOrg.await()

        val fullTime = reproTime + avergingTime + scoringTime  + cullTime
                             + prePair.right + postPair.right

        //////////////////////////////////////////////////////


        stats.CYCLEDURATIONS.add(fullTime)
        stats.FILIALGENTIME.add(reproTime)
        stats.AVERAGINGTIME.add(avergingTime)
        stats.SCORINGTIME.add(scoringTime)
        stats.CULLTIME.add(cullTime)
        stats.CULLCUTOFF.add(cutoff)
        stats.ORGCULLED.add(culled)
        stats.AF_INIT.add(preMateAvg)
        stats.AF_POST_M.add(postMateAvg)
        stats.AF_POST_C.add(pop.avgFitness)
        stats.BESTORGCHANGE.add(if (prePair.left != postPair.left) 1 else 0)

        endCyleStats(tempLog, pop, year, fullTime, reproTime, filial, avergingTime,
                scoringTime, cullTime, cutoff, culled, preMateAvg, postMateAvg, prePair,
                postPair)

        return@runBlocking
    }

    fun <O : Organism> endCyleStats(log: InternalLog, pop: Population<O>, year: Int,
                                    fullTime: Long, reproTime: Long,
                                    filial: Population<O>, avergingTime: Long,
                                    scoringTime: Long, cullTime: Long, cutoff: Double,
                                    culled: Int, preMateAvg: Double, postMateAvg: Double,
                                    prePair: Pair<O, Long>, postPair: Pair<O, Long>) {
        log.log("${pop.name} Post Cycle $year Analysis:")
        log.log("Cycle Duration=${Timer.format(fullTime)}")
        log.log("Filial generation time : ${Timer.format(reproTime)} (${reproTime.percent(
                fullTime)}%)")
        log.log("\tsize=${filial.size}")
        log.log("Averaging time : ${Timer.format(avergingTime)} (${avergingTime.percent(
                fullTime)}%)")
        log.log("Scoring time : ${Timer.format(scoringTime)} (${scoringTime.percent(
                fullTime)}%)")
        log.log("Cull time : ${Timer.format(cullTime)} (${cullTime.percent(fullTime)}%)")
        log.log("Culling cutoff=$cutoff")
        log.log("Orgs Culled=$culled")
        log.log("Avg Fitness:")
        log.log("\tpre-mating=$preMateAvg")
        log.log("\tpost-mating=$postMateAvg")
        log.log("\tPost-culling=${pop.avgFitness}")
        log.log("Best Organism Pre-Filial Time : ${Timer.format(
                prePair.right)} (${prePair.right.percent(fullTime)}%)")
        log.log("Best Organism Post-Filial Time: ${Timer.format(
                postPair.right)} (${postPair.right.percent(fullTime)}%)")
        log.log("Best Org Pre-filial =${prePair.left}")
        log.log("Best Org Post-Filial=${postPair.left}\n")
        this.log.ingestAndPrint(log)
    }

    /** @return The organism with the highest fitness (or first if same score) */
    private fun <O: Organism> bestOrg(list: List<O>) : O {
        var best: O = list[0]
        list.forEach { if (it.fitness > best.fitness) best = it }
        return best
    }

    /**
     * Score each organism in a population
     *
     * @param pop The population to score
     */
    private suspend fun <O: Organism> scorePopulation(pop: Population<O>) {
        val sc = environment.scoreMap[pop.name]!!
        /*
        for (i in 0 until pop.size) {
            slog("Time $i=" +
                    measureTimeMillis { pop[i].fitness = score(pop[i], sc) }.toDouble()
                            .div(1_000.0))
        }
        */
        List(pop.size) { i ->
            launch (READ_POOL) {
                pop[i].fitness = score(pop[i], sc)
            }
        }.joinAll()
        //*/
    }
}
