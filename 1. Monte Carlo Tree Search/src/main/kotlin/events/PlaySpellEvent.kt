class PlaySpellEvent(
        private val playedSpell: Spell,
        private val onMinion: Minion,
        private val player: Player,
        private val adversary: Player
) : Event {
    override fun execute() {
        playedSpell.positiveAction?.execute(onMinion)
        playedSpell.negativeAction?.execute(onMinion)
        player.takeDown(playedSpell)
        if (onMinion.hp <= 0) {
            player.takeDown(onMinion)
            adversary.takeDown(onMinion)
        }
//        print()
    }

    override fun rollBack() {
        if (onMinion.hp <= 0) {
            player.resurrect(onMinion)
            adversary.resurrect(onMinion)
        }
        player.resurrectSpell(playedSpell)
        playedSpell.positiveAction?.rollBack(onMinion)
        playedSpell.negativeAction?.rollBack(onMinion)
    }

    override fun print() {
        println("${player.hero.name}: Cast a spell ${playedSpell.name} on ${onMinion.name}")
    }
}