class AttackHeroEvent(
        private val attacker: Minion,
        private val victim: Hero
) : Event {
    override fun execute() {
        victim.hp -= attacker.attack
        attacker.playedInRound = true
//        print()
    }

    override fun rollBack() {
        victim.hp += attacker.attack
        attacker.playedInRound = false
    }

    override fun print() {
        println("Attack hero ${victim.name} with ${attacker.name}")
    }

    val attack: Int
        get() = attacker.attack

    val endGame: Boolean
        get() = victim.hp <= 0
}