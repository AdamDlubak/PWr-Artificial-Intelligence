class AttackMinionEvent(
        private val attacker: Minion,
        private val victim: Minion,
        private val attackerPlayer: Player,
        private val victimPlayer: Player
) : Event {
    override fun execute() {
        victim.hp -= attacker.attack
        attacker.hp -= victim.attack
        attacker.playedInRound = true

        if (attacker.hp <= 0) {
            attackerPlayer.takeDown(attacker)
        }
        if (victim.hp <= 0) {
            victimPlayer.takeDown(victim)
        }
//        print()
    }

    override fun rollBack() {
        if (attacker.hp <= 0) {
            attackerPlayer.resurrect(attacker)
        }
        if (victim.hp <= 0) {
            victimPlayer.resurrect(victim)
        }

        victim.hp += attacker.attack
        attacker.hp += victim.attack
        attacker.playedInRound = false
    }

    override fun print() {
        println("Attack minion ${victim.name} with ${attacker.name}")
    }

    val controlFactor: Int
        get() {
            var attackerStats = attackerPlayer.tableCards.filterIsInstance(Minion::class.java).sumBy { it.attack + it.hp }
            var victimStats = victimPlayer.tableCards.filterIsInstance(Minion::class.java).sumBy { it.attack + it.hp }
            attackerStats -= victim.attack
            victimStats -= attacker.attack
            return attackerStats - victimStats
        }

}