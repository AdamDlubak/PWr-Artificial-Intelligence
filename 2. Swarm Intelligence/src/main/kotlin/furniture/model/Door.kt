package furniture.model

import evaluation.Boundaries

data class Door(
        val centerX: Double = 0.0,
        val centerY: Double = 350.0,
        val width: Double = Dimensions.DOOR.width,
        val height: Double = Dimensions.DOOR.height
) : Boundaries {
    override val left = centerX - width / 2
    override val right = centerX + width / 2
    override val top = centerY + height / 2
    override val bottom = centerY - height / 2
}