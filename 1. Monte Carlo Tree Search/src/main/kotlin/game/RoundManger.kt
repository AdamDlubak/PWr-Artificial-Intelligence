data class RoundManger(val playerOne: Player, val playerTwo: Player, val turn: Int = 0) {
    companion object {
        const val MAX_MAGIC_POINT = 10
    }

    fun copy(playerOne: Player, playerTwo: Player): RoundManger {
        return RoundManger(playerOne, playerTwo, turns)
    }

    var turns = turn
    var roundNumber = (turn / 2) + 1
    var currentPlayer: Player
    var enemyPlayer: Player

    init {
        if (turns.rem(2) == 0) {
            currentPlayer = playerOne
            enemyPlayer = playerTwo
        } else {
            currentPlayer = playerTwo
            enemyPlayer = playerOne
        }
    }

    fun firstRound(playerOne: Player, playerTwo: Player) {
        setupPlayers(playerOne, playerTwo)
        giveInitialSetOfCards(playerOne, playerTwo)
        loadMagicPoint(currentPlayer, enemyPlayer)
    }

    private fun setupPlayers(currentPlayer: Player, enemyPlayer: Player) {
        this.currentPlayer = currentPlayer
        this.enemyPlayer = enemyPlayer
    }

    fun newRound() {
        incrementRoundNumber()
        loadMagicPoint(currentPlayer, enemyPlayer)
        unlockMinions(currentPlayer)
        unlockMinions(enemyPlayer)
    }

    private fun incrementRoundNumber() {
        roundNumber += 1
    }

    private fun giveInitialSetOfCards(playerOne: Player, playerTwo: Player) {
        playerOne.pickCards(3)
        playerTwo.pickCards(4)
    }

    private fun loadMagicPoint(playerOne: Player, playerTwo: Player) {
        val roundMagicPoint = if (roundNumber < 10) roundNumber else MAX_MAGIC_POINT
        playerOne.magicPoint = roundMagicPoint
        playerTwo.magicPoint = roundMagicPoint
    }

    private fun unlockMinions(player: Player) {
        player.tableCards
                .filterIsInstance(Minion::class.java)
                .onEach { it.playedInRound = false }
    }

    fun endTurn() {
        turns += 1
        currentPlayer = enemyPlayer.also { enemyPlayer = currentPlayer }
        decrementMinionSnooze(currentPlayer.tableCards)
        if (turns.rem(2) == 0) newRound()
        if (roundNumber != 1)
            pickCardOrDecrementHP(currentPlayer)
    }
    fun endTurnWithoutCardPicking() {
        turns += 1
        currentPlayer = enemyPlayer.also { enemyPlayer = currentPlayer }
        decrementMinionSnooze(currentPlayer.tableCards)
        if (turns.rem(2) == 0) newRound()
//        if (roundNumber != 1)
//        pickCardOrDecrementHP(currentPlayer)
    }

    private fun pickCardOrDecrementHP(player: Player) {
        if (player.deck.isEmpty()) player.hero.hp -= 1
        else player.pickCards(1)
    }
    fun decrementHP(player: Player) {
        player.hero.hp -= 1
    }
    fun incrementHP(player: Player) {
        player.hero.hp += 1
    }
    private fun decrementMinionSnooze(tableCards: List<Card>) {
        tableCards.filterIsInstance(Minion::class.java)
                .onEach { it.isSnoozed -= 1 }
    }
    private fun incerementMinionSnooze(tableCards: List<Card>) {
        tableCards.filterIsInstance(Minion::class.java)
                .onEach { it.isSnoozed += 1 }
    }
}