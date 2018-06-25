package furniture.model

enum class Dimensions(
        val width: Double,
        val height: Double
) {
    TABLE(100.0, 100.0),
    CHAIR(35.0, 35.0),
    DOOR(100.0, 100.0),
    ROOM(500.0, 500.0),
    SOFA(200.0, 100.0),
    TV(70.0, 30.0),
    WARDROBE(50.0, 20.0),
    WINDOW(100.0, 100.0)
}