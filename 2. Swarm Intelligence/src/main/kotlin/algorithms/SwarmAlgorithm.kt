package algorithms

import FileSaver
import algorithms.model.Animal
import testFunctions.BenchmarkFunction
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

interface SwarmAlgorithm {
    val benchmarkFunction: BenchmarkFunction
    val problemDimension: Int
    val minSpace: Double
    val maxSpace : Double
    val random : Random

    fun Random.nextDouble(min: Double, max: Double) = ThreadLocalRandom.current().nextDouble(min, max)
    fun randomVector(min : Double, max: Double) : Array<Double> {
        return Array(problemDimension) { ThreadLocalRandom.current().nextDouble(min, max) }
    }

    fun generatePopulation(populationSize: Int): Array<*>
    fun newAnimal() : Animal
    fun execute(generations: Int, populationSize: Int, fileToSave: FileSaver?, fileToSaveBests: FileSaver?) : ArrayList<*>
    fun saveResults(fileToSave: FileSaver?, fileToSaveBests: FileSaver?, population : Array<Animal>, currentBest : Animal, generation: Int) {
        if(fileToSave != null && fileToSaveBests != null) {
            fileToSave.appendNextBatch(population, generation)
            fileToSaveBests.appendNextBest(currentBest, generation)
        }
    }
}
