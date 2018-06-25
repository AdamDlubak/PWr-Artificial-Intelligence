package algorithms

import FileSaver
import algorithms.model.Animal
import algorithms.model.Particle
import testFunctions.BenchmarkFunction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class PSOAlgorithm(
        override val benchmarkFunction: BenchmarkFunction,
        override val problemDimension: Int,
        override val minSpace: Double,
        override val maxSpace : Double,
        override val random: Random = Random(),

        private val minVel : Double = 0.0,
        private val maxVel : Double = 1.0,
        private val c1 : Double = 2.0,
        private val c2 : Double = 2.0,
        private val w : Double = 0.1
) : SwarmAlgorithm, BenchmarkFunction by benchmarkFunction {

    override fun generatePopulation(populationSize: Int): Array<Particle> {
        return Array(populationSize) { newAnimal() }
    }
    override fun newAnimal(): Particle {
        var position = randomVector(minSpace, maxSpace)
        var cost = benchmarkFunction(position, problemDimension)
        return Particle(
                position = position,
                cost = cost,
                localBestPosition = position.copyOf(),
                localBestCost = cost,
                velocity = randomVector(minVel, maxVel)
        )
    }
    override fun execute(generations: Int, populationSize: Int, fileToSave: FileSaver?, fileToSaveBests: FileSaver?): ArrayList<Particle> {
        val population = generatePopulation(populationSize)
//        evaluation(population)
        var globalBest = findBest(population).copyAnimal()

        val bestGenerations: ArrayList<Particle> = arrayListOf()
        saveResults(fileToSave, fileToSaveBests, population as Array<Animal>, globalBest, 0)


        for (t in 1..generations) {
            for (particle in population) {
                updateVelocity(particle, globalBest)
                updatePosition(particle)
                particle.cost = evaluation(particle.position)
                if (particle.cost < particle.localBestCost) {
                    particle.localBestPosition = particle.position.copyOf()
                    particle.localBestCost = evaluation(particle.localBestPosition)
                    if (particle.localBestCost < globalBest.localBestCost) {
                        globalBest = particle.copyAnimal()
                    }
                }
            }
            saveResults(fileToSave, fileToSaveBests, population as Array<Animal>, globalBest, t)
//            println("${benchmarkFunction(globalBest.position, problemDimension)}")
            bestGenerations.add(globalBest)
        }
        return bestGenerations
    }

    private fun evaluation(position: Array<Double>): Double {
        var penalty = 0.0
        for (i in 0 until position.size) {
            if (abs(position[i]) > maxSpace) penalty += abs(position[i] - maxSpace)
        }
        return benchmarkFunction(position, problemDimension) + penalty
    }
    private fun updatePosition(particle: Particle) {
        for (i in 0 until particle.position.size) {
            particle.position[i] = particle.position[i] + particle.velocity[i]
        }
    }
    private fun updateVelocity(particle: Particle, globalBest: Particle) {
        for (i in 0 until problemDimension) {
            val piXi = particle.localBestPosition[i] - particle.position[i]
            val gXi = globalBest.localBestPosition[i] - particle.position[i]

            particle.velocity[i] = w*particle.velocity[i] + (c1 * random.nextDouble() * piXi) + (c2 * random.nextDouble() * gXi)
        }
    }
    private fun findBest(population: Array<Particle>, currentBest: Particle? = null): Particle {
        val rank = population.toMutableList()
        rank.sortBy { it.localBestCost }
        val best = rank.first()
        if (currentBest == null || (best.localBestCost <= currentBest.localBestCost)) return best
        return currentBest
    }
}