package evaluation

import furniture.model.*
import testFunctions.BenchmarkFunction
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.tan


class Evaluator : RoomEvaluator {
    override fun isFurnitureInRoom(room: Room, furniture: Boundaries): Boolean {
        return furniture.left in 0.0..room.width
                && furniture.right in 0.0..room.width
                && furniture.top in 0.0..room.height
                && furniture.bottom in 0.0..room.height
    }

    override fun isFurnitureOverlap(furnitureOne: Boundaries, furnitureTwo: Boundaries): Boolean {
        return furnitureOne.left < furnitureTwo.right
                && furnitureOne.right > furnitureTwo.left
                && furnitureOne.top > furnitureTwo.bottom
                && furnitureOne.bottom < furnitureTwo.top
    }

    override fun isGoodTvAngle(tv: TV, sofa: Sofa): Boolean {
        return if (!(tv.centerX == sofa.centerX || tv.centerY == sofa.centerY)) {
            val b = abs(tv.centerX - sofa.centerX)
            val a = abs(tv.centerY - sofa.centerY)

            tan(b / a) <= (sqrt(3.0) / 3)
        } else {
            true
        }
    }

    override fun isWindowCovered(window: Window, furniture: Furniture): Boolean {
        return isFurnitureOverlap(window as Boundaries, furniture as Boundaries)
    }

    override fun isDoorCovered(door: Door, furniture: Furniture): Boolean {
        return isFurnitureOverlap(door as Boundaries, furniture as Boundaries)
    }
}


class RoomFunction : BenchmarkFunction {

    private val eval = Evaluator()
    override fun benchmarkFunction(position: Array<Double>, dimension: Int, sphereRay: Double): Double {
        val furniture = mutableListOf<Furniture>(
                Table(position[0], position[1],
                        position[0] - Dimensions.TABLE.width / 2,
                        position[0] + Dimensions.TABLE.width / 2,
                        position[1] - Dimensions.TABLE.height / 2,
                        position[1] + Dimensions.TABLE.height / 2),
                Sofa(position[2], position[3],
                    position[2] - Dimensions.SOFA.width / 2,
                    position[2] + Dimensions.SOFA.width / 2,
                    position[3] - Dimensions.SOFA.height / 2,
                    position[3] + Dimensions.SOFA.height / 2),
                Chair(position[4], position[5],
                        position[4] - Dimensions.CHAIR.width / 2,
                        position[4] + Dimensions.CHAIR.width / 2,
                        position[5] - Dimensions.CHAIR.height / 2,
                        position[5] + Dimensions.CHAIR.height / 2),
                Chair(position[6], position[7],
                        position[6] - Dimensions.CHAIR.width / 2,
                        position[6] + Dimensions.CHAIR.width / 2,
                        position[7] - Dimensions.CHAIR.height / 2,
                        position[7] + Dimensions.CHAIR.height / 2),
                Chair(position[8], position[8],
                        position[8] - Dimensions.CHAIR.width / 2,
                        position[8] + Dimensions.CHAIR.width / 2,
                        position[9] - Dimensions.CHAIR.height / 2,
                        position[9] + Dimensions.CHAIR.height / 2),
                Chair(position[10], position[11],
                        position[10] - Dimensions.CHAIR.width / 2,
                        position[10] + Dimensions.CHAIR.width / 2,
                        position[11] - Dimensions.CHAIR.height / 2,
                        position[11] + Dimensions.CHAIR.height / 2),
                Wardrobe(position[12], position[13],
                        position[12] - Dimensions.WARDROBE.width / 2,
                        position[12] + Dimensions.WARDROBE.width / 2,
                        position[13] - Dimensions.WARDROBE.height / 2,
                        position[13] + Dimensions.WARDROBE.height / 2),
                Wardrobe(position[14], position[15],
                        position[14] - Dimensions.WARDROBE.width / 2,
                        position[14] + Dimensions.WARDROBE.width / 2,
                        position[15] - Dimensions.WARDROBE.height / 2,
                        position[15] + Dimensions.WARDROBE.height / 2),
                Wardrobe(position[16], position[17],
                        position[16] - Dimensions.WARDROBE.width / 2,
                        position[16] + Dimensions.WARDROBE.width / 2,
                        position[17] - Dimensions.WARDROBE.height / 2,
                        position[17] + Dimensions.WARDROBE.height / 2),
                TV(position[18], position[19],
                        position[18] - Dimensions.TV.width / 2,
                        position[18] + Dimensions.TV.width / 2,
                        position[19] - Dimensions.TV.height / 2,
                        position[19] + Dimensions.TV.height / 2)
        )

        var penalty = 0.0


        // Check if all furniture are in room
        for (fur in furniture) {
            if (!eval.isFurnitureInRoom(Room(), fur as Boundaries)) {
                penalty += 100.0
            }
        }

        // Check if no overlaps
        for ((i, fur_i) in furniture.withIndex()) {
            for ((j, fur_j) in furniture.withIndex()) {
                if (i == j) continue
                if (eval.isFurnitureOverlap(fur_i as Boundaries, fur_j as Boundaries)) {
                    penalty += 20.0
                }
            }
        }

        // Check no hidden window
        for (fur in furniture) {
            if (eval.isWindowCovered(Window(), fur)) {
                penalty += 10.0
            }
        }


        // Check no hidden door
        for (fur in furniture) {
            if (eval.isDoorCovered(Door(), fur)) {
                penalty += 10.0
            }
        }


        // Check TV angle
        if (!eval.isGoodTvAngle(furniture.last() as TV, furniture[1] as Sofa)) {
            penalty += 10.0
        }


        // Check max carpet's radius
        if(penalty == 0.0){
            val carpetRadius = furniture.filter { !it.canBeOnCarpet }.map { getCarpetDistance(Carpet(), it) }
            val carpetRad = carpetRadius.min()?: 0.0
            penalty -= (Room().height/2) - carpetRad
        }

//        println(penalty)
        return penalty
    }


    private fun getCarpetDistance(carpet: Carpet, furniture: Furniture): Double {
        furniture as Boundaries

        return if((carpet.centerX in furniture.left..furniture.right) && (carpet.centerY in furniture.bottom..furniture.top)) {
            0.0
        }
        else if ((carpet.centerX in furniture.left..furniture.right) && carpet.centerY !in furniture.bottom..furniture.top) {
            return if(furniture.top < carpet.centerY)
                carpet.centerY - furniture.top
            else {
                furniture.bottom - carpet.centerY
            }
        }
        else if ((carpet.centerX !in furniture.left..furniture.right) && carpet.centerY in furniture.bottom..furniture.top) {
            return if(furniture.left > carpet.centerX)
                furniture.left - carpet.centerX
            else {
                carpet.centerX - furniture.right
            }
        }
        // Left upper corner
        else if (furniture.centerX < carpet.centerX && furniture.centerY > carpet.centerY) {
            sqrt(pow(furniture.right - carpet.centerX, 2.0) + pow(furniture.bottom - carpet.centerY, 2.0))
        }

        // Right upper corner
        else if (furniture.centerX > carpet.centerX && furniture.centerY > carpet.centerY) {
            sqrt(pow(furniture.left - carpet.centerX, 2.0) + pow(furniture.bottom - carpet.centerY, 2.0))
        }

        // Right bottom corner
        else if (furniture.centerX > carpet.centerX && furniture.centerY < carpet.centerY) {
            sqrt(pow(furniture.left - carpet.centerX, 2.0) + pow(furniture.top - carpet.centerY, 2.0))
        }

        // Left bottom corner
        else if (furniture.centerX < carpet.centerX && furniture.centerY < carpet.centerY) {
            sqrt(pow(furniture.right - carpet.centerX, 2.0) + pow(furniture.top - carpet.centerY, 2.0))
        }
        else if (furniture.centerX == carpet.centerX && furniture.centerY > carpet.centerY) {
            furniture.bottom - carpet.centerY
        }
        else if (furniture.centerX == carpet.centerX && furniture.centerY < carpet.centerY) {
            carpet.centerY - furniture.top
        }
        else if (furniture.centerX < carpet.centerX && furniture.centerY == carpet.centerY) {
            carpet.centerX - furniture.right
        }
        else if (furniture.centerX > carpet.centerX && furniture.centerY == carpet.centerY) {
            furniture.left - carpet.centerX
        }
        else {
          0.0
        }
    }

}