package algorithms.model

class Particle(
        position: Array<Double>,
        cost: Double,
        var localBestPosition : Array<Double>,
        var localBestCost : Double,
        var velocity: Array<Double>
) : Animal(position, cost){

    fun copyAnimal() : Particle {
        return Particle(this.position.copyOf(), this.cost, this.localBestPosition.copyOf(), this.localBestCost, this.velocity.copyOf())
    }
}