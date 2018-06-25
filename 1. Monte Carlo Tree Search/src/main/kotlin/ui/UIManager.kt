interface UIManager {
    fun showBoard(playerOne: Player, playerTwo: Player, currentPlayer: Player, roundNumber: Int)
    fun showPossibleEvents(possibleEvents: List<Event>)
    fun readInput(): Int
    fun finishGame(winnerName: String)
    fun showError(message: String)
}