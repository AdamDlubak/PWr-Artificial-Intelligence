package testFunctions

interface BenchmarkFunction {
    fun benchmarkFunction(position: Array<Double>, dimension: Int, sphereRay: Double = 0.0) : Double
}