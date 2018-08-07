package com.ampro.evemu.emulation

import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.constants.Alphabet.*
import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.Population
import com.ampro.evemu.organism.ReproductiveType.SEX
import com.ampro.evemu.ribonucleic.score
import com.ampro.evemu.util.*
import kotlinx.coroutines.experimental.*
import kotlin.system.measureTimeMillis

internal val envNamer = SequentialNamer(listOf(E,N,V), 100, 2)

/**
 *
 *
 * @author Jonathan Augustine
 * @since 3.0
 */
class SimpleEmulator(val name: String = envNamer.next(),
                     val environment: SimpleEnvironment,
                     var years: Int = 100) : Runnable {

    private val log = InternalLog(name,
            years * environment.populations.size * 10,
            true, true)

    private val cycleDurations = ArrayList<Long>(years * environment.populations.size)
    private val yearDurations  = ArrayList<Long>(years)

    override fun run() {
        log.logAndPrint("Starting Emulator...")
        log.logAndPrint("Calculating Parent Fitness...")
        val startupTime = measureTimeMillis {
            runBlocking {
                List(environment.size) { i ->
                    launch (FIXED_POOL) { scorePopulation(environment[i]) }
                }.joinAll()
            }
        }
        log.logAndPrint("...DONE (time=${startupTime/1_000})")
        environment.populations.forEach { log.logAndPrint(it) }
        log.logAndPrint("")

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
            log.logAndPrint("Avg Cycle duration=${
            cycleDurations.let { Timer.format(it.sum()/it.size) }}", true)
            log.logAndPrint("Avg Year duration=${yearDurations.let {
                Timer.format(it.sum()/it.size) }}", true)
            log.logAndPrint("Full Run Duration = $fullTimer", true)
            environment.populations.forEach { log.logAndPrint(it, true) }
            log.logAndPrint("Current Time<<<<>>>>$NOW\n\n", true)
            cycleDurations.clear()
        }

        log.toFile()
    }

    /**
     *
     */
    private fun <O: Organism> yearCycle(pop: Population<O>, year: Int) = runBlocking {
        val tempLog = InternalLog("${pop.name} yearCycle", showName = true)

        if (pop.isEmpty) {
            tempLog.log("Population ${pop.name} is empty.")
            cycleDurations.add(0L)
            return@runBlocking
        } else if (pop[0].reproductiveType == SEX && pop.size < 2) {
            pop.clear()
            tempLog.log("Population ${pop.name} died out.")
            cycleDurations.add(0L)
            return@runBlocking
        }

        val preMateAvg = pop.avgFitness

        var postMateAvg = 0.0

        val bestPreFOrg= async (FIXED_POOL) {
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

        tempLog.log("${pop.name} Post Cycle $year Analysis:")
        tempLog.log("Cycle Duration=${Timer.format(fullTime)}")
        tempLog.log("Filial generation time : ${
        Timer.format(reproTime)} (${reproTime.percent(fullTime)}%)")
        tempLog.log("\tsize=${filial.size}")
        tempLog.log("Averaging time : ${
        Timer.format(avergingTime)} (${avergingTime.percent(fullTime)}%)")
        tempLog.log("Scoring time : ${
        Timer.format(scoringTime)} (${scoringTime.percent(fullTime)}%)")
        tempLog.log("Cull time : ${
        Timer.format(cullTime)} (${cullTime.percent(fullTime)}%)")
        tempLog.log("Culling cutoff=$cutoff")
        tempLog.log("Orgs Culled=$culled")
        tempLog.log( "Avg Fitness:")
        tempLog.log("\tpre-mating=$preMateAvg")
        tempLog.log("\tpost-mating=$postMateAvg")
        tempLog.log("\tPost-culling=${pop.avgFitness}")
        tempLog.log("Best Organism Pre-Filial Time : ${
        Timer.format(prePair.right)} (${prePair.right.percent(fullTime)}%)")
        tempLog.log("Best Organism Post-Filial Time: ${
        Timer.format(postPair.right)} (${postPair.right.percent(fullTime)}%)")
        tempLog.log("Best Org Pre-filial =${prePair.left}")
        tempLog.log("Best Org Post-Filial=${postPair.left}\n")
        log.ingestAndPrint(tempLog)
        cycleDurations.add(fullTime)
        return@runBlocking
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
        val scoredCodons = environment.scoreMap[pop.name]!!
        List(pop.size) { i ->
            launch(FIXED_POOL) { pop[i].fitness = score(pop[i], scoredCodons) }
        }.joinAll()
    }
}
