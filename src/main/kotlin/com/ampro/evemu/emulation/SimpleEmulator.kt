package com.ampro.evemu.emulation

import com.ampro.evemu.FIXED_POOL
import com.ampro.evemu.organism.Organism
import com.ampro.evemu.organism.Population
import com.ampro.evemu.organism.ReproductiveType.SEX
import com.ampro.evemu.ribonucleic.score
import com.ampro.evemu.util.InternalLog
import com.ampro.evemu.util.NOW
import com.ampro.evemu.util.Timer
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.runBlocking
import kotlin.collections.ArrayList
import kotlin.coroutines.experimental.coroutineContext
import kotlin.system.measureTimeMillis

/**
 *
 *
 * @author Jonathan Augustine
 * @since 3.0
 */
class SimpleEmulator(val name: String = SimpleEmulator::class.java.simpleName,
                     val environment: SimpleEnvironment, var years: Int = 100)
    : Runnable {

    private val log = InternalLog(name,
            initSize = years * environment.populations.size * 10,
            showThread = true, showName = true)

    val cycleDurations = ArrayList<Long>(years * environment.populations.size)
    val yearDurations  = ArrayList<Long>(years)

    override fun run() {
        log.logAndPrint("Starting Emulator...")
        log.logAndPrint("Calculating Parent Fitness...")
        val startupTime = measureTimeMillis {
            runBlocking {
                List(environment.size) { i ->
                    async(FIXED_POOL) { scorePopulation(environment[i]) }
                }.awaitAll()
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
            log.logAndPrint("Avg Cycle duration=${cycleDurations.let {
                Timer.format(it.sum()/it.size) }}", true)
            log.logAndPrint("Avg Year duration=${yearDurations.let {
                Timer.format(it.sum()/it.size) }}", true)
            log.logAndPrint("Full Run Duration = $fullTimer", true)
            environment.populations.forEach { log.logAndPrint(it, true) }
            log.logAndPrint("Current Time<<<<>>>>$NOW\n\n", true)
        }

        log.toFile()
    }

    private fun <O: Organism>yearCycle(pop: Population<O>, year: Int) = runBlocking {
        val tempLog = InternalLog("${pop.name} yearCycle", showName = true)

        if (pop.isEmpty) {
            tempLog.logAndPrint("Population ${pop.name} is empty.")
            return@runBlocking
        } else if (pop[0].reproductiveType == SEX && pop.size < 2) {
            pop.clear()
            tempLog.logAndPrint("Population ${pop.name} died out.")
            return@runBlocking
        }

        val preMateAvg = pop.avgFitness
        tempLog.log("Pre Filial Average Fitness : $preMateAvg")

        tempLog.log("Creating filial generation...")
        tempLog.log("\tNumber of Offspring = ${pop.size / 2}")
        tempLog.log("\tChildren per pair = ${4} Min Age of Parent = ${5}")

        var postMateAvg: Double = 0.0

        val bestPreFilialOrg = async (FIXED_POOL) { bestOrg(pop.population.toList()) }

        val filial = ArrayList<O>()
        val reproTime = measureTimeMillis {
            filial.addAll(pop.reproduce(pop.size / 2, 4, 5))
        }
        val time = reproTime + measureTimeMillis {
            pop.addAll(filial)
            tempLog.log("...Filial generation generated.")
            postMateAvg = pop.avgFitness

            scorePopulation(pop)

            /* TODO
            println("\nPre-Culling average " + generation
                    + " Organism Score>>" + filial!!.getAverageFitness())
            System.out.println("\nCulling " + filial!!.getGeneration()
                    + "\nPopulation Size before culling>>"
                    + filial!!.getPopulation().size())
            if (i % 1000 == 0) {
                val numToCull = filial!!.getPopulation().size() / (5 + Random().nextInt(11))
                val cutOff = filial!!.getAverageFitness() - 1f * filial!!.getScoreDeviation()
                println(
                        "\nCulling $numToCull Below>>>$cutOff")
                Die_Off.cull(filial, numToCull, cutOff)
            } else {
                val numToCull = filial!!.getPopulation().size() / (20 + Random().nextInt(21))
                val cutOff = filial!!.getAverageFitness() - 2.5f * filial!!.getScoreDeviation()
                println("Culling $numToCull Below>>>$cutOff")
                Die_Off.cull(filial, numToCull, cutOff)
            }
            System.out.println("Population Size after culling>>" + filial!!.getPopulation().size())
            println("\nChecking Best Specimen of Generation "
                    + generation + "\n")
            */

            val it = pop.population.listIterator()
            while (it.hasNext()) {
                val o = it.next()
                //System.out.println(o);
                if (o.fitness < pop.avgFitness - 3 * pop.stdDeviation
                    || o.age >= 20 || o.fitness <= 0) {
                    o.die()
                    it.remove()
                }
                o.age++
            }
        }

        val bestPostFilialOrg = async (FIXED_POOL) { bestOrg(pop.population) }

        tempLog.logAndPrint("${pop.name} Post Cycle Analysis: ")
        tempLog.logAndPrint("cycle $year Duration=${time/1_000}")
        tempLog.logAndPrint("size=${pop.size}")
        tempLog.logAndPrint( "Avg Fitness: pre-mating=$preMateAvg" +
                " post-mating=$postMateAvg} Post-culling=${pop.avgFitness}"
        )
        tempLog.logAndPrint("Best Organism :")
        tempLog.logAndPrint("\tPre-filial=${bestPreFilialOrg.await()}")
        tempLog.logAndPrint("\tPost-Filial=${bestPostFilialOrg.await()}\n\n")
        log.ingest(tempLog)
        cycleDurations.add(time)
        return@runBlocking
    }

    /** @return The organism with the highest fitness (or first if same score) */
    private fun <O: Organism> bestOrg(list: List<O>) : O {
        var best: O = list[0]
        list.forEach {
            if (it.fitness > best.fitness) {
                best = it
            }
        }
        return best
    }

    /**
     * Score each organism in a population
     *
     * @param pop The population to score
     */
    private suspend fun <O: Organism> scorePopulation(pop: Population<O>) {
        List(pop.size) { i ->
            async(coroutineContext) {
                pop[i].fitness = score(pop[i], environment.scoreMap[pop.name]!!)
            }
        }.awaitAll()
    }

}
