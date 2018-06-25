class ConsoleManager : UIManager {
    override fun showPossibleEvents(possibleEvents: List<Event>) {
        println("You can: ")
        possibleEvents.forEachIndexed { index, event ->
            print("\t$index. "); event.print()
        }
    }

    override fun readInput() = readLine()!!.split(' ').map(String::toInt).elementAt(0)

    override fun finishGame(winnerName: String) {
        println("End Game! The winner is $winnerName \n\n")
    }

    override fun showBoard(playerOne: Player, playerTwo: Player, currentPlayer: Player, roundNumber: Int) {
        println("\n\n")
        println("------------------Round number: $roundNumber")
        println("------------------Current player: ${currentPlayer.hero.name}")
        println()
        print("Used cards: "); showCards(playerOne.usedCards)
        println()
        if(playerOne.hero.hp < 0) println(" --- ${playerOne.hero.name} --- HP: 0  MANA: ${playerOne.magicPoint} --- ")
        else println(" --- ${playerOne.hero.name} --- HP: ${playerOne.hero.hp}  MANA: ${playerOne.magicPoint} --- ")
        print("In hand: "); showCards(playerOne.handCards)
        println()
        println()
        println()
        showCards(playerOne.tableCards)
        println()
        println()
        showCards(playerTwo.tableCards)
        println()
        println()
        println()
        print("In hand: "); showCards(playerTwo.handCards)
        println()
        if(playerTwo.hero.hp < 0) println(" --- ${playerTwo.hero.name} --- HP: 0  MANA: ${playerTwo.magicPoint} --- ")
        else println(" --- ${playerTwo.hero.name} --- HP: ${playerTwo.hero.hp}  MANA: ${playerTwo.magicPoint} --- ")
        print("Used cards: ")
        showCards(playerTwo.usedCards)
        println()
        println()
    }

    private fun showCards(cards: List<Card>) {
        cards.asSequence().forEach(Card::showCard)
    }

    override fun showError(message: String) {
        println(message)
    }
}