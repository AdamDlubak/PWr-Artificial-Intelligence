package testFunctions

class RastriginFunction : BenchmarkFunction {

    override fun benchmarkFunction(position: Array<Double>, dimension: Int, sphereRay: Double) : Double {

        var sum : Double
        // Check if point belong to restriction
        if(sphereRay != 0.0) {
            sum = 0.0
            for (i in 0 until dimension) sum += Math.pow(position[i], 2.0)
            val sphereExtent = Math.pow(sphereRay, 2.0)
            if (sum > sphereExtent) return sphereExtent - sum
        }
        // Compute benchmarkFunction value
        sum = 0.0
        for(i in 0 until dimension) sum += (Math.pow(position[i], 2.0)) - (10 * Math.cos(2* Math.PI * position[i]))
        return 10 * dimension + sum
    }

}