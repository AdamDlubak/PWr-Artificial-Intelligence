class WearDown : SpecialSkill {
    override fun execute(player: Player) {
        player.tableCards.filterIsInstance(Minion::class.java).forEach {
            it.hp -= 1
        }
    }

    override fun rollBack(player: Player) {
        player.tableCards.filterIsInstance(Minion::class.java).forEach {
            it.hp += 1
        }
    }
}