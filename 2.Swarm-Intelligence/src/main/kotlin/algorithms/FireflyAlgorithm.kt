package algorithms

import FileSaver
import algorithms.model.Animal
import algorithms.model.Firefly
import testFunctions.BenchmarkFunction
import java.lang.Math.pow
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt


class FireflyAlgorithm (
        override val benchmarkFunction: BenchmarkFunction,
        override val problemDimension: Int,
        override val minSpace: Double,
        override val maxSpace : Double,
        override val random: Random = Random(),

        private var alpha: Double = 1.0,
        private val beta: Double = 0.4,
        private val gamma: Double = 0.5
) : SwarmAlgorithm, BenchmarkFunction by benchmarkFunction {


    override fun generatePopulation(populationSize: Int) = Array(populationSize) { newAnimal() }
    override fun newAnimal(): Firefly {
        return Firefly(
                position = randomVector(minSpace, maxSpace),
                brightness = Double.MAX_VALUE,
                cost = Double.MAX_VALUE
        )
    }
    override fun execute(generations: Int, populationSize: Int, fileToSave: FileSaver?, fileToSaveBests: FileSaver?): ArrayList<Firefly> {
        val population = generatePopulation(populationSize)
        evaluation(population)
        var globalBest = findBest(population).copyAnimal()

        val bestGenerations: ArrayList<Firefly> = arrayListOf()
        saveResults(fileToSave, fileToSaveBests, population as Array<Animal>, globalBest, 0)

        for (t in 1..generations) {
            for (i in 0 until population.size) {
                for (j in 0 until population.size) {
                    if (population[j].cost < population[i].cost) moveAnimal(population[i], population[j])
                }
                evaluation(population[i])
                if(globalBest.cost > population[i].cost) globalBest = population[i].copyAnimal()
            }
            alpha -= alpha/(2*generations)
            population.sortBy { it.cost }

            saveResults(fileToSave, fileToSaveBests, population as Array<Animal>, globalBest, t)
//            println("${benchmarkFunction(globalBest.position, problemDimension)}")
            bestGenerations.add(globalBest)
        }
        return bestGenerations
    }

    private fun moveAnimal(fireflyI: Firefly, fireflyJ: Firefly) {
        var distance = (0 until fireflyI.position.size).sumByDouble { pow(fireflyI.position[it] - fireflyJ.position[it], 2.0) }
        distance = sqrt(distance)

        for (i in 0 until fireflyI.position.size) {
            val secondPart = beta / (1 + gamma * pow(distance, 2.0)) * (fireflyJ.position[i] - fireflyI.position[i])
            val thirdPart = alpha*(random.nextDouble() - 0.5)
            fireflyI.position[i] = fireflyI.position[i] + secondPart + thirdPart
        }
    }
    private fun evaluation(population: Array<Firefly>) {
        population.forEach {
            it.cost = benchmarkFunction(it.position, problemDimension)
            it.brightness = (1 / it.cost)
        }
    }
    private fun evaluation(firefly: Firefly) {
        firefly.cost = benchmarkFunction(firefly.position, problemDimension)
        firefly.brightness = (1 / firefly.cost)
    }
    fun findBest(population: Array<Firefly>): Firefly {
        val rank = population.toMutableList()
        rank.sortBy { benchmarkFunction(it.position, problemDimension) }
        return rank.first()
    }
}