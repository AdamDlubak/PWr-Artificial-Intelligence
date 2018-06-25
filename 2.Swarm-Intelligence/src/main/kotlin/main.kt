import algorithms.BatAlgorithm
import algorithms.FireflyAlgorithm
import algorithms.PSOAlgorithm
import algorithms.SwarmAlgorithm
import algorithms.model.Animal
import javafx.application.Application
import testFunctions.RastriginFunction

import tornadofx.*
import view.MainView

fun main(args: Array<String>) {

    val isRoomTest = false
    val algorithmNumber = 3   // 1: Firefly    2: Bat    3 and other: PSO

    if(isRoomTest) renderView(args)
    else {
        val generations = 100
        val populationSize = 50
        val problemDimension = 2
        val minSpace = -5.0
        val maxSpace = 5.0
        val benchmarkFunction = RastriginFunction()

        val name = when (algorithmNumber) {
            1 -> "Firefly"
            2 -> "Bat"
            else -> "PSO"
        }

                val fileToSave = createFileToSaveResults(false, name, generations, populationSize)
                val fileToSaveBests = createFileToSaveResults(true, name, generations, populationSize)

                val algorithm: SwarmAlgorithm = when (algorithmNumber) {
                    1 -> FireflyAlgorithm(benchmarkFunction, problemDimension, minSpace, maxSpace)
                    2 -> BatAlgorithm(benchmarkFunction, problemDimension, minSpace, maxSpace)
                    else -> PSOAlgorithm(benchmarkFunction, problemDimension, minSpace, maxSpace)
                }

                val result: ArrayList<Animal> = algorithm.execute(generations, populationSize, fileToSave, fileToSaveBests) as ArrayList<Animal>
                val bestResult: Animal = result.last()

            println("Best position: ${bestResult.position.contentToString()}")
            println("Function value in best position: ${benchmarkFunction.benchmarkFunction(bestResult.position, problemDimension)}\n")

            }


        }


fun createFileToSaveResults(isBest : Boolean, name : String, maxGeneration : Int, populationCount : Int): FileSaver {
    val fileSaver : FileSaver
    if (isBest) {
        fileSaver = FileSaver("${name}_Best_${maxGeneration}_$populationCount")
        fileSaver.createFileWithHeader("Generation,X,Y,Cost")
    } else {
        fileSaver = FileSaver("${name}_${maxGeneration}_$populationCount")
        fileSaver.createFileWithHeader()
    }
    return fileSaver
}

fun renderView(args: Array<String>) {
    Application.launch(SwarmApp::class.java, *args)
}

class SwarmApp : App(MainView::class)