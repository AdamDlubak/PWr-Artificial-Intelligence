package furniture.model

abstract class Furniture(
        var centerX: Double,
        var centerY: Double,
        val width: Double,
        val height: Double,
        val canBeOnCarpet: Boolean
)