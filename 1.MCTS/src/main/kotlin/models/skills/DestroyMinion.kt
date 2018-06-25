class DestroyMinion : SpellAction {

    override fun execute(minion: Minion) {
        minion.hp -= 100
    }

    override fun rollBack(minion: Minion) {
        minion.hp += 100
    }

}