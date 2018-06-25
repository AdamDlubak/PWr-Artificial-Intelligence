package furniture.model

import evaluation.Boundaries

data class Window(
        val centerX: Double = 0.0,
        val centerY: Double = 250.0,
        val width: Double = Dimensions.WINDOW.width,
        val height: Double = Dimensions.WINDOW.height
) : Boundaries {
    override val left = centerX - width / 2
    override val right = centerX + width / 2
    override val top = centerY + height / 2
    override val bottom = centerY - height / 2
}