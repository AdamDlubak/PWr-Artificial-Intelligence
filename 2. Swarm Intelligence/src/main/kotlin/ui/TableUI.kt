package ui

import furniture.model.Dimensions
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class TableUI(x: Double = 0.0, y: Double = 0.0) : Rectangle() {
    init {
        fill = Color.BROWN
        width = Dimensions.TABLE.width
        height = Dimensions.TABLE.height
        this.x = x
        this.y = y
    }
}


