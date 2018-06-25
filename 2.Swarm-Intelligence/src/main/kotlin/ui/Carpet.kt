package ui

import javafx.scene.paint.Color
import javafx.scene.shape.Circle

class Carpet(r: Double) : Circle() {
    init {
        fill = Color.GOLDENROD
        this.centerX = 250.0
        this.centerY = 250.0
        this.radius = r
    }
}