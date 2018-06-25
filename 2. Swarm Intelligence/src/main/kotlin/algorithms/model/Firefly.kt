package algorithms.model

class Firefly(
        position: Array<Double>,
        var brightness: Double,
        cost: Double
) : Animal(position, cost) {

    fun copyAnimal() : Firefly {
        return Firefly(this.position.copyOf(), this.brightness, this.cost)
    }
}