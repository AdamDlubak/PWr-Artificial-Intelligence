package furniture.model

import evaluation.Boundaries

class Sofa(
        centerX: Double,
        centerY: Double,
        left : Double,
        right : Double,
        top: Double,
        bottom: Double,
        width: Double = Dimensions.SOFA.width,
        height: Double = Dimensions.SOFA.height,
        canBeOnCarpet: Boolean = false
) : Furniture(centerX, centerY, width, height, canBeOnCarpet), Boundaries {
    override val left = centerX - width / 2
    override val right = centerX + width / 2
    override val top = centerY + height / 2
    override val bottom = centerY - height / 2
}