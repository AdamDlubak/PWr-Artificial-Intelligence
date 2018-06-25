package evaluation

import furniture.model.*

interface RoomEvaluator {
    fun isWindowCovered(window: Window, furniture: Furniture): Boolean
    fun isDoorCovered(door: Door, furniture: Furniture): Boolean
    fun isGoodTvAngle(tv: TV, sofa: Sofa): Boolean
    fun isFurnitureOverlap(furnitureOne: Boundaries, furnitureTwo: Boundaries): Boolean
    fun isFurnitureInRoom(room: Room, furniture: Boundaries): Boolean
}