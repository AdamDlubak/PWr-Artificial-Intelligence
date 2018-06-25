package ui

import furniture.model.Dimensions
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class TVUI(x: Double= 0.0, y: Double= 0.0) : Rectangle() {
    init {
        fill = Color.CHARTREUSE
        width = Dimensions.TV.width
        height = Dimensions.TV.height
        this.x = x
        this.y = y
    }
}


