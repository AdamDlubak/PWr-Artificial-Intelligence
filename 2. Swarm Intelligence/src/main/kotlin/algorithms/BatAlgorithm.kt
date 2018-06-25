package algorithms

import FileSaver
import algorithms.model.Animal
import algorithms.model.Bat
import testFunctions.BenchmarkFunction
import java.lang.Math.pow
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.exp

class BatAlgorithm(
        override val benchmarkFunction: BenchmarkFunction,
        override val problemDimension: Int,
        override val minSpace: Double,
        override val maxSpace : Double,
        override val random: Random = Random(),

        private val fMin : Double = 0.0,
        private val fMax : Double = 1.0,
        private val alpha : Double = 0.5,
        private val lambda : Double = 0.1,
        private val initLoudness : Double = 1.0,
        private var boundaryCount : Int = 0,
        private var boundaryScapeCount : Int = 0
) : SwarmAlgorithm, BenchmarkFunction by benchmarkFunction {

    override fun generatePopulation(populationSize: Int): Array<Bat> {
        return Array(populationSize) { newAnimal() }
    }
    override fun newAnimal(): Bat {
        return Bat(
                position = randomVector(minSpace, maxSpace),
                velocity = randomVector(minSpace, maxSpace),
                loudness = initLoudness,
                pulse = random.nextDouble(0.0, 1.0),
                frequency = random.nextDouble(fMin, fMax),
                cost = Double.MAX_VALUE
        )
    }
    override fun execute(generations: Int, populationSize: Int, fileToSave: FileSaver?, fileToSaveBests: FileSaver?): ArrayList<Bat> {
        val population = generatePopulation(populationSize)
        evaluation(population)
        var globalBest = findBest(population).copyAnimal()

        val bestGenerations : ArrayList<Bat> = arrayListOf()
        saveResults(fileToSave, fileToSaveBests, population as Array<Animal>, globalBest, 0)

        for (t in 1..generations) {
            for (i in 0 until population.size) {

                updateFrequency(population[i])
                updateVelocity(population[i], globalBest)

                val batCandidate = population[i].copyAnimal()
                batCandidate.position = updatePosition(population[i])
                evaluation(batCandidate)
                if (random.nextDouble() < batCandidate.pulse) {
                    batCandidate.position = localSearch(batCandidate, globalBest, getLoudnessAverage(population))
                }
                batCandidate.position = forceBoundaryOnVector(batCandidate.position)
                evaluation(batCandidate)
                evaluation(population[i])

                if(random.nextDouble() < population[i].loudness && batCandidate.cost < population[i].cost) {
                    population[i] = batCandidate.copyAnimal()
                    updatePulse(population[i], t)
                    updateLoudness(population[i], t)
                }
                val bestToCheck = findBest(population)
                if(globalBest.cost > bestToCheck.cost) globalBest = bestToCheck.copyAnimal()
            }
            saveResults(fileToSave, fileToSaveBests, population as Array<Animal>, globalBest, t)
//            println("${benchmarkFunction(globalBest.position, problemDimension)}")
            bestGenerations.add(globalBest)
        }
//        println(boundaryCount)
//        println(boundaryScapeCount)
        return bestGenerations
    }

    private fun localSearch(bat: Bat, globalBest: Bat, loudnessAverage: Double): Array<Double> {
        for(i in 0 until problemDimension) {
            bat.position[i] = globalBest.position[i] + loudnessAverage * random.nextDouble(-1.0, 1.0)
        }
        return bat.position
    }
    private fun evaluation(population: Array<Bat>) {
        population.forEach {
            it.cost = benchmarkFunction(it.position, problemDimension)
        }
    }
    private fun evaluation(bat : Bat) {
        bat.cost = benchmarkFunction(bat.position, problemDimension)
    }
    private fun updateVelocity(bat: Bat, globalBest: Bat) {
        for(i in 0 until problemDimension) {
            bat.velocity[i] = bat.velocity[i] + bat.frequency * (bat.position[i] - globalBest.position[i])
        }
        bat.velocity = forceBoundaryOnVector(bat.velocity)
    }
    private fun updatePosition(bat: Bat) : Array<Double> {
        for(i in 0 until problemDimension) {
            bat.position[i] += bat.velocity[i]
        }
        bat.position = forceBoundaryOnVector(bat.position)
        return bat.position
    }
    private fun updateLoudness(bat : Bat, t : Int) {
        bat.loudness = initLoudness * pow(alpha, t.toDouble())
    }
    private fun updatePulse(bat: Bat, t: Int) {
        bat.pulse = 1 - exp(-lambda * t)
    }
    private fun updateFrequency(bat: Bat) {
        bat.frequency = fMin + (fMax - fMin) * random.nextDouble()
    }
    private fun getLoudnessAverage(population: Array<Bat>) : Double {
        val sum = population.sumByDouble { it.loudness }
        return (sum / population.size)
    }
    private fun findBest(population: Array<Bat>) : Bat {
        val rank = population.toMutableList()
        rank.sortBy { it.cost }
        return rank.first()
    }
    private fun forceBoundaryOnVector(vector : Array<Double>): Array<Double> {
        for(i in 0 until problemDimension) {
            vector[i] = forceBoundaryOnValue(vector[i])
        }
        return vector
    }
    private fun forceBoundaryOnValue(value: Double): Double {
        boundaryCount++
        return when {
            value > maxSpace -> {
                boundaryScapeCount++
                maxSpace
            }
            value < minSpace -> {
                boundaryScapeCount++
                minSpace
            }
            else -> value
        }
    }
}

