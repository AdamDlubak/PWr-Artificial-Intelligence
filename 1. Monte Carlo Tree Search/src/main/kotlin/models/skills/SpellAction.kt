interface SpellAction {
    fun execute(minion: Minion)
    fun rollBack(minion: Minion)
}