class PlayMinionEvent(
        private val playedMinion: Minion,
        private val player: Player,
        private val adversary: Player
) : Event {
    override fun execute() {
        player.putOnTable(playedMinion)
        playedMinion.specialSkill?.execute(adversary)
//        print()
    }

    override fun rollBack() {
        player.returnToHand(playedMinion)
        playedMinion.specialSkill?.rollBack(adversary)
    }

    override fun print() {
        println("Play ${playedMinion.name} on table")
    }

    val factor: Int
        get() {
            var tempFactor = (playedMinion.attack * playedMinion.hp) / playedMinion.cost
            tempFactor + if (playedMinion.specialSkill != null) 10 else 0
            return tempFactor
        }
}