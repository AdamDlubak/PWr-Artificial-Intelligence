package view

import furniture.model.Dimensions
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class RoomUI(
        width: Double = Dimensions.ROOM.width,
        height: Double = Dimensions.ROOM.height
) : Rectangle(width, height) {
    init {
        fill = Color.GRAY
    }
}