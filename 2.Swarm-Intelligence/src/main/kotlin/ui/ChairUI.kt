package ui

import furniture.model.Dimensions
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class ChairUI(x: Double = 0.0, y: Double = 0.0) : Rectangle() {
    init {
        fill = Color.YELLOWGREEN
        width = Dimensions.CHAIR.width
        height = Dimensions.CHAIR.height
        this.x = x
        this.y = y
    }
}