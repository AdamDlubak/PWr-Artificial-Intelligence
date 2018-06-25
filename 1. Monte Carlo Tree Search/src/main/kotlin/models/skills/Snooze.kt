class Snooze : SpecialSkill {
    override fun execute(player: Player) {
        player.tableCards.filterIsInstance(Minion::class.java).forEach {
            it.isSnoozed += 4
        }
    }

    override fun rollBack(player: Player) {
        player.tableCards.filterIsInstance(Minion::class.java).forEach {
            it.isSnoozed -= 4
        }
    }

}