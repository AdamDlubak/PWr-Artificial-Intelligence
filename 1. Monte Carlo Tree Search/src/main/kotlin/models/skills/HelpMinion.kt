class HelpMinion : SpellAction {
    override fun execute(minion: Minion) {
        minion.hp += 2
    }

    override fun rollBack(minion: Minion) {
        minion.hp -= 2
    }
}