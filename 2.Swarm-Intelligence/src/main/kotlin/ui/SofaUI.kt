package ui

import furniture.model.Dimensions
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class SofaUI(x: Double = 0.0, y: Double = 0.0) : Rectangle() {
    init {
        fill = Color.BEIGE
        width = Dimensions.SOFA.width
        height = Dimensions.SOFA.height
        this.x = x
        this.y = y
    }
}