import java.util.*

data class GameState(
        val playerOne: Player,
        val playerTwo: Player,
        val round: RoundManger
) {
    fun copy(): GameState {
        val playerOneCopy = playerOne.copy()
        val playerTwoCopy = playerTwo.copy()
        return GameState(playerOneCopy, playerTwoCopy, round.copy(playerOneCopy, playerTwoCopy))
    }
}

data class Node(
        val gameState: GameState,
        val parent: Node?,
        var isAfterTurnNode: Boolean = false,
        var playerOneWins: Int = 0,
        var playerTwoWins: Int = 0
) {

    var childArray = mutableSetOf<Node>()
        set(value) {
            isLeaf = value.isEmpty()
            field = value
        }

    val allPlays: Int
        get() = playerOneWins + playerTwoWins

    val currentPlayerWins: Int
        get() {
            return if (gameState.round.currentPlayer == gameState.playerOne) {
                playerOneWins
            } else {
                playerTwoWins
            }
        }

    var isLeaf = false
    fun print() {
        parent?.let {
            println(
                    """"
                I am: ${this.hashCode()} and have: ${this.childArray.size} children
                My parent is: ${parent.hashCode()} and has: ${parent.childArray.size} children
            """)
        }
    }

    fun printLine() {
        parent?.let {
            println("${hashCode()},$playerOneWins,$playerTwoWins,${childArray.size},${parent.hashCode()},${parent.childArray.size}")
        }
    }

    val id = Random().nextInt()
}