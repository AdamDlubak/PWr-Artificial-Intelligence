import algorithms.model.Animal
import java.io.File

class FileSaver(fileName: String, ext: String = ".csv") {
    private val folderName = "results/"
    private val file = folderName + fileName + ext




    fun createFileWithHeader(header: String = "Generation,X,Y") {
        File(file).delete()
        File(file).appendText("$header\n" )
    }

    fun appendNextBatch(population: Array<Animal>, generation: Int) {
        population.forEach {
            File(file).appendText("$generation,${it.position[0]},${it.position[1]}\n")
        }
    }
    fun appendNextBest(animal: Animal, generation: Int) {
        File(file).appendText("$generation,${animal.position[0]},${animal.position[1]},${animal.cost}\n")
    }
}