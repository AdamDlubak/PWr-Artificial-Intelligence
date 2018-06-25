import java.util.concurrent.TimeoutException

fun main(args: Array<String>) {
    var numberOfIterations = 10
    var i = 0
    while(i <=numberOfIterations) {
        try {
            print("__ GAME: $i \n")
            val game = initGame()
            game.startWithMCTS(ControlPlayer())
            i++

        } catch (e: TimeoutException) {
            continue
        }
    }

    //game.startGame()

    //game.startGameWithGreedy(AgresivePlayer(), AgresivePlayer())

    //game.startSimulation()
}

fun initGame(): Game {
    val playerOne = Player(Hero("Bob"), Deck.newShuffled())
    val playerTwo = Player(Hero("Alice"), Deck.newShuffled())

    return Game(playerOne, playerTwo)
}


