package ui

import furniture.model.Dimensions
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class WardrobeUI(x: Double = 0.0, y: Double = 0.0) : Rectangle() {
    init {
        fill = Color.MISTYROSE
        width = Dimensions.WARDROBE.width
        height = Dimensions.WARDROBE.height
        this.x = x
        this.y = y
    }
}