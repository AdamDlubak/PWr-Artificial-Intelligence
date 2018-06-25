class SnoozeMinion : SpellAction {
    override fun execute(minion: Minion) {
        minion.isSnoozed += 2
    }

    override fun rollBack(minion: Minion) {
        minion.isSnoozed -= 2
    }

}