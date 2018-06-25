package algorithms.model

class Bat(
        position: Array<Double>,       // x
        var velocity: Array<Double>,   // v
        var loudness: Double,          // A
        var pulse: Double,             // r
        var frequency: Double,         // f
        cost: Double
) : Animal(position, cost) {

    fun copyAnimal() : Bat {
        return Bat(this.position.copyOf(), this.velocity.copyOf(), this.loudness, this.pulse, this.frequency, this.cost)
    }
}