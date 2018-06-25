import java.util.concurrent.TimeoutException

class Game(
        private var playerOne: Player,
        private var playerTwo: Player,
        var round: RoundManger = RoundManger(playerOne, playerTwo),
        private val uiManager: UIManager = ConsoleManager()
) : GameLoop {

    lateinit var winner: Player
    var endGame: Boolean = false
    var lineNumbers = 0
    fun startWithMCTS(greedyPlayer: GreedyPlayer) {
        round.firstRound(playerOne, playerTwo)
        lineNumbers = 0
        //renderView()
        while (endGame.not()) {

//            renderView()
            try {
                val mcts = MCTS(greedyPlayer.toString())
                val newGameState = mcts.work(this)

                setGameState(newGameState)
                //renderView()
                if(this.playerOne.hero.hp <= 0) {
                    dispatchEvent(EndGameEvent(playerTwo, uiManager))
                    winner = playerTwo
                    endGame = true
                    mcts.saveWinner(2)
                }
                if(this.playerTwo.hero.hp <= 0) {
                    dispatchEvent(EndGameEvent(playerOne, uiManager))
                    winner = playerOne
                    endGame = true
                    mcts.saveWinner(1)
                }

            }
            catch(e: TimeoutException) {
              throw e
            }
            //----------------- End MCTS Turn

            if(endGame) break


            //----------------- Greedy Player Turn
            do {
                val event = chooseEvent(greedyPlayer)
                //event.print()
                dispatchEvent(event)
            } while (event !is EndTurnEvent && event !is EndGameEvent)
            //----------------- End Greedy Player Turn

            if(this.playerOne.hero.hp <= 0) {
                dispatchEvent(EndGameEvent(playerTwo, uiManager))
                winner = playerTwo
                endGame = true
                val mcts = MCTS(greedyPlayer.toString())
                mcts.saveWinner(2)
            }
            if(this.playerTwo.hero.hp <= 0) {
                dispatchEvent(EndGameEvent(playerOne, uiManager))
                winner = playerOne
                endGame = true
                val mcts = MCTS(greedyPlayer.toString())
                mcts.saveWinner(1)
            }
        }
        //renderView()
        //dispatchEvent(EndGameEvent(winner, uiManager))
    }

    fun startGameWithGreedy(greedyPlayerOne: GreedyPlayer, greedyPlayerTwo: GreedyPlayer) {
        var doneActionsOne = 0
        var doneActionsTwo = 0
        while (endGame.not()) {
            do {
                val event = chooseEvent(greedyPlayerOne)
                dispatchEvent(event)
                doneActionsOne++
               // renderView()
            } while (event !is EndTurnEvent && event !is EndGameEvent)

            do {
                val event = chooseEvent(greedyPlayerTwo)
                dispatchEvent(event)
                doneActionsTwo++
                //renderView()
            } while (event !is EndTurnEvent && event !is EndGameEvent)
        }
//        println()
//        println(doneActionsOne)
//        println(doneActionsTwo)
//        println(this.winner.hero.name)
//        println()
    }

    fun startGame() {
        round.firstRound(playerOne, playerTwo)
        renderView()
        endGame = false

        while (true) {
            val event = waitForEvent()
            dispatchEvent(event)
            renderView()
        }
    }



    private fun chooseEvent(player: GreedyPlayer): Event {
        val possibleEvents = getPossibleEvents(round.currentPlayer, round.enemyPlayer, round)
        return player.chooseEvent(possibleEvents)
    }

    override fun waitForEvent(): Event {
        val possibleEvents = getPossibleEvents(round.currentPlayer, round.enemyPlayer, round)
        uiManager.showPossibleEvents(possibleEvents)
        val eventIndex = uiManager.readInput()
        return possibleEvents[eventIndex]
    }

    override fun dispatchEvent(event: Event) {
        when (event) {
            is AttackMinionEvent -> event.execute()
            is AttackHeroEvent -> {
                event.execute()
                if (event.endGame) {
                    winner = round.currentPlayer
                    endGame = true
                }
            }
            is PlayMinionEvent -> event.execute()
            is PlaySpellEvent -> event.execute()
            is EndTurnEvent -> {
                event.execute()
                if (round.currentPlayer.hero.hp <= 0) {
                    winner = round.enemyPlayer
                    endGame = true
                }
            }
            is EndGameEvent -> event.execute()
        }
    }

    override fun renderView() {
        uiManager.showBoard(playerOne, playerTwo, round.currentPlayer, round.roundNumber)
    }

    fun getGameState(): GameState {
        val playerOneCopy = playerOne.copy()
        val playerTwoCopy = playerTwo.copy()
        return GameState(
                playerOneCopy,
                playerTwoCopy,
                round.copy(playerOneCopy, playerTwoCopy)
        )
    }

    fun setGameState(gameState: GameState) {
        playerOne = gameState.playerOne
        playerTwo = gameState.playerTwo
        round = gameState.round
    }
}

fun getPossibleEvents(player: Player, adversary: Player, round: RoundManger): List<Event> {
    if (round.currentPlayer != player) return emptyList()

    val events = mutableListOf<Event>()
    events.addAll(getPossibleMinionAttacks(player, adversary))
    events.addAll(getPossibleHeroAttacks(player, adversary))
    events.addAll(getPossibleMinionPlayCards(player, adversary))
    events.addAll(getPossibleSpellPlayCards(player, adversary))
    events.add(EndTurnEvent(round))
    return events
}

private fun getPossibleMinionAttacks(attacker: Player, victim: Player): List<AttackMinionEvent> {
    val activeAttackerMinions = attacker.tableCards
            .filterIsInstance(Minion::class.java)
            .filter { it.isSnoozed <= 0 }
            .filter { !it.playedInRound }

    val activeVictimMinions = victim.tableCards
            .filterIsInstance(Minion::class.java)

    val possibleMinionAttacks = mutableListOf<AttackMinionEvent>()

    for (attackerMinion in activeAttackerMinions) {
        for (victimMinion in activeVictimMinions) {
            possibleMinionAttacks.add(AttackMinionEvent(attackerMinion, victimMinion, attacker, victim))
        }
    }

    return possibleMinionAttacks
}

private fun getPossibleHeroAttacks(attacker: Player, victim: Player): MutableList<AttackHeroEvent> {
    val activeAttackerMinions = attacker.tableCards
            .filterIsInstance(Minion::class.java)
            .filter { it.isSnoozed <= 0 }
            .filter { !it.playedInRound }

    val possibleHeroAttacks = mutableListOf<AttackHeroEvent>()

    for (attackerMinion in activeAttackerMinions) {
        possibleHeroAttacks.add(AttackHeroEvent(attackerMinion, victim.hero))
    }

    return possibleHeroAttacks
}

private fun getPossibleMinionPlayCards(attacker: Player, victim: Player): List<PlayMinionEvent> {
    if (attacker.magicPoint == 0) return emptyList()
    if (attacker.tableCards.size == 7) return emptyList()

    val possibleMinionsToPlay = attacker.handCards
            .filterIsInstance(Minion::class.java)
            .filter { it.cost <= attacker.magicPoint }


    val possibleMinionCardsEvents = mutableListOf<PlayMinionEvent>()
    for (minion in possibleMinionsToPlay) {
        possibleMinionCardsEvents.add(PlayMinionEvent(minion, attacker, victim))
    }

    return possibleMinionCardsEvents
}

private fun getPossibleSpellPlayCards(attacker: Player, victim: Player): List<PlaySpellEvent> {
    if (attacker.magicPoint == 0) return emptyList()

    val spellCardEvents = mutableListOf<PlaySpellEvent>()

    if (attacker.tableCards.isNotEmpty()) {
        val possiblePositiveSpellCards = attacker.handCards
                .filterIsInstance(Spell::class.java)
                .filter { it.positiveAction != null }
                .filter { it.cost <= attacker.magicPoint }

        for (spell in possiblePositiveSpellCards) {
            for (minion in attacker.tableCards.filterIsInstance(Minion::class.java))
                spellCardEvents.add(PlaySpellEvent(spell, minion, attacker, victim))
        }
    }

    if (victim.tableCards.isNotEmpty()) {
        val possibleNegativeSpellCards = attacker.handCards
                .filterIsInstance(Spell::class.java)
                .filter { it.negativeAction != null }
                .filter { it.cost <= attacker.magicPoint }

        for (spell in possibleNegativeSpellCards) {
            for (minion in victim.tableCards.filterIsInstance(Minion::class.java))
                spellCardEvents.add(PlaySpellEvent(spell, minion, attacker, victim))
        }
    }

    return spellCardEvents
}