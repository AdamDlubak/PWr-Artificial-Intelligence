data class Hero(
        val name: String,
        var hp: Int = 20
) {
    fun copy(): Hero = Hero(name, hp)
}
