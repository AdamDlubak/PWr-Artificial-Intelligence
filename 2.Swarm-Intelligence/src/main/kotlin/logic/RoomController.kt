package logic

import algorithms.BatAlgorithm
import algorithms.FireflyAlgorithm
import algorithms.PSOAlgorithm
import algorithms.SwarmAlgorithm
import algorithms.model.Animal
import evaluation.RoomFunction
import furniture.model.Dimensions
import furniture.model.Table
import javafx.scene.shape.Circle
import tornadofx.Controller
import tornadofx.FXEvent
import javafx.scene.shape.Rectangle
import ui.*


class RoomController : Controller() {

    private val algorithmNumber = 3   // 1: Firefly    2: Bat    3 and other: PSO
    private val benchmarkFunction = RoomFunction()
//    private val generations = 20
    private val populationSize = 20
    private val problemDimension = 20
    private val minSpace = 0.0
    private val maxSpace = 500.0


    fun compute() {
        val algorithm: SwarmAlgorithm = when (algorithmNumber) {
            1 -> FireflyAlgorithm(benchmarkFunction, problemDimension, minSpace, maxSpace)
            2 -> BatAlgorithm(benchmarkFunction, problemDimension, minSpace, maxSpace)
            else -> PSOAlgorithm(benchmarkFunction, problemDimension, minSpace, maxSpace)
        }


        var b = 0
        for (i in 10 until 101 step 10) {
            var generations = i
            do {
            val result: ArrayList<Animal> = algorithm.execute(generations, populationSize, null, null) as ArrayList<Animal>
            val bestResult: Animal = result.last()
            if(bestResult.cost < 0.0 && bestResult.cost > -250.0) print(" ${(250 - (bestResult.cost * -1))}, ")
                //        println("Best result is: " + bestResult.cost)
                if(bestResult.cost < 0.0 && bestResult.cost > -250.0)  {
                    showOnView(Carpet(250 - (bestResult.cost * -1)))
                    drawFurnitures(bestResult)
                }
            } while(bestResult.cost >= 0.0 || bestResult.cost <= -250.0)

        }
//        else compute()
    }
    private fun drawFurnitures(animal: Animal) {
        showOnView(TableUI(animal.position[0] - Dimensions.TABLE.width/2, animal.position[1] - Dimensions.TABLE.height/2))
        showOnView(SofaUI(animal.position[2] - Dimensions.SOFA.width/2, animal.position[3] - Dimensions.SOFA.height/2))
        showOnView(ChairUI(animal.position[4] - Dimensions.CHAIR.width/2, animal.position[5] - Dimensions.CHAIR.height/2))
        showOnView(ChairUI(animal.position[6] - Dimensions.CHAIR.width/2, animal.position[7] - Dimensions.CHAIR.height/2))
        showOnView(ChairUI(animal.position[8] - Dimensions.CHAIR.width/2, animal.position[9] - Dimensions.CHAIR.height/2))
        showOnView(ChairUI(animal.position[10] - Dimensions.CHAIR.width/2, animal.position[11] - Dimensions.CHAIR.height/2))
        showOnView(WardrobeUI(animal.position[14] - Dimensions.WARDROBE.width/2, animal.position[15] - Dimensions.WARDROBE.height/2))
        showOnView(WardrobeUI(animal.position[16] - Dimensions.WARDROBE.width/2, animal.position[17] - Dimensions.WARDROBE.height/2))
        showOnView(WardrobeUI(animal.position[12] - Dimensions.WARDROBE.width/2, animal.position[13] - Dimensions.WARDROBE.height/2))
        showOnView(TVUI(animal.position[18] - Dimensions.TV.width/2, animal.position[19] - Dimensions.TV.height/2))
    }
    private fun showOnView(furniture: Rectangle) {
        fire(FurnitureRenderEvent(furniture))
    }
    private fun showOnView(furniture: Circle) {
        fire(FurnitureRenderEventC(furniture))
    }
}

class FurnitureRenderEvent(
        val furniture: Rectangle
) : FXEvent()
class FurnitureRenderEventC(
        val furniture: Circle
) : FXEvent()