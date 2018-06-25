import events.PickCardEvent
import java.io.File
import java.util.concurrent.TimeoutException
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.system.exitProcess


private const val TURN_TIME = 10000

class MCTS(val greedyPlayerName: String) {

    lateinit var root: Node

    var time: Long = 0
    var roundNumber = 0
    var childAmount = 0
    var exploredNodes = 0
    var leafsCount = 0
    var maxLeafDepth = 0
    var minLeafDepth = 0
    var EXT = ".txt"
    var FILE_NAME = "MCTS_vs_${greedyPlayerName}_$TURN_TIME"
    //var FILE_NAME = "${greedyPlayerName}_vs_MCTS_${TURN_TIME}"
    var FILE_NAME_WINS = FILE_NAME + "_wins"
    var maxRoundsNumber = 20
    var lineNumbers = 0
    private var startTime: Long = 0
    fun work(game: Game): GameState {

        root = Node(game.getGameState(), null)
        startTime = System.currentTimeMillis() //fetch starting time

        while ((System.currentTimeMillis() - startTime) <= TURN_TIME) {
            try {
                val v1 = treePolicy(root)
                val delta = defaultPolicy(v1)
                backPropagation(v1, delta)
            } catch (e: TimeoutException) {
                File(FILE_NAME + EXT).appendText("Here was time out exception")
                File(FILE_NAME + EXT).appendText(System.lineSeparator())
                //throw e
                break
            }
        }

        // Statistics
        time = System.currentTimeMillis() - startTime
        roundNumber = game.round.roundNumber
        childAmount = countChildren(root, 0)
        leafsCount = countLeafs(root, 0)
        maxLeafDepth = countMaxDepthLeaf(root, 0)
        minLeafDepth = countMinDepthLeaf(root, 0)

        // Print Statistics
        //  printTree(root)
        //println("Time: $time")
        //println("Round: $roundNumber")
        //println("Wins: ${root.playerOneWins}\t${root.playerTwoWins} ")
        //println("Child Amount: $childAmount")
        //println("Explored Nodes Amount: $exploredNodes")
        //println("Leafs Amount: $leafsCount")
        //println("Max leaf depth: $maxLeafDepth")
        //println("Min leaf depth: $minLeafDepth")


        File(FILE_NAME + EXT).appendText("$time\t$roundNumber\t${root.playerOneWins}\t${root.playerTwoWins}\t$childAmount\t$exploredNodes\t$leafsCount\t$maxLeafDepth\t$minLeafDepth")
        game.lineNumbers++
        lineNumbers = game.lineNumbers
        File(FILE_NAME + EXT).appendText(System.lineSeparator())
//
//  File(FILE_NAME + EXT).printWriter().use { out ->
//            out.appendln("$time,$roundNumber,${root.playerOneWins},${root.playerTwoWins},$childAmount,$exploredNodes,$leafsCount,$maxLeafDepth,$minLeafDepth")
//        }

        // -----------------------------------
        return findBestChild(root, 0.0).gameState
    }


    // Selection & Expansion in one function
    private fun treePolicy(node: Node): Node {
        var tmpNode = node

        // Do while is not a leaf
        while (!tmpNode.isNonTerminal()) {

            // If is not expanded, select and expand
            if (tmpNode.isNotExpanded()) {
                exploredNodes++
                if (tmpNode.isAfterTurnNode) { // Expand with card draw
                    with(tmpNode.gameState) {
                        // If deck is empty, only one state is possible
                        if (round.currentPlayer.deck.isEmpty()) {
                            round.decrementHP(round.currentPlayer)
                            tmpNode.childArray.add(Node(this.copy(), tmpNode))
                            round.incrementHP(round.currentPlayer)
                            return tmpNode.childArray.first()
                        } else {
                            // else generate X nodes, where X is number of different cards in deck
                            val deckDictionary = round.currentPlayer.deck.getGroupped()
                            deckDictionary.forEach { name, cards ->
                                val event = PickCardEvent(round.currentPlayer, cards.first())
                                event.execute()
                                val newNode = Node(this.copy(), tmpNode)
                                for (i in 0 until cards.size) {
                                    tmpNode.childArray.add(newNode)
                                }
                                event.rollBack()
                            }
                            return tmpNode.childArray.shuffled().first()
                        }
                    }

                } else {
                    expand(tmpNode)
                    return findBestChild(tmpNode) // Return node to simulation (default policy)
                }
            }
            tmpNode = findBestChild(tmpNode)
        }
        return tmpNode
    }

    private fun checkChildNodesQuantity(node: Node, quantity: Int): Int {
        var tmpQuantity = quantity
        if (node.childArray.isNotEmpty()) {
            node.childArray.forEach { tmpQuantity += checkChildNodesQuantity(it, tmpQuantity) }
            return tmpQuantity + 1
        }
        return 1
    }

    private fun Node.isNonTerminal() = isLeaf
    private fun Node.isNotExpanded() = childArray.isEmpty() // Do not have children yet

    private fun expand(parent: Node) {
        parent.childArray = expandNode(parent).toMutableSet()
    }

    private fun expandNode(node: Node): List<Node> {

        val nodesList = mutableListOf<Node>()
        try {
            if ((System.currentTimeMillis() - startTime) > 7 * TURN_TIME) {
                throw TimeoutException()
            }
            val possibleEvents =
                    if (node.gameState.round.turns % 2 == 0) {
                        getPossibleEvents(node.gameState.playerOne, node.gameState.playerTwo, node.gameState.round)
                    } else {
                        getPossibleEvents(node.gameState.playerTwo, node.gameState.playerOne, node.gameState.round)
                    }

           // println("###$$$$ ${node.id}   ${possibleEvents.size}")

            possibleEvents.forEach {
                if (it is EndTurnEvent) {
                    val nodeTMP = Node(node.gameState.copy(), node, true)
                    val endTurnEvent = EndTurnEvent(nodeTMP.gameState.round)
                    endTurnEvent.executeWithoutCardPicking()
                    nodesList += nodeTMP
                    //println("^^^^")
                    return nodesList
                }
                it.execute()
                nodesList += expandNode(node)
                it.rollBack()
            }
        } catch (e: Exception) {
            //println("Error: $e")
            //println("Child Amount: $childAmount")
            //println("Explored Nodes Amount: $exploredNodes")
            //println(System.currentTimeMillis() - startTime)
            throw TimeoutException()

        }
        return nodesList.toList()
    }

//    fun Event.executeWithRollBack(block: () -> List<Node>): List<Node> {
//        execute()
//        val node = block()
//        rollBack()
//        return node
//    }


    private fun findBestChild(node: Node, c: Double = (1 / sqrt(2.0))): Node {
        return if (node.childArray.isEmpty()) {
            node
        } else {
            val allNotPlayed = node.childArray.filter { it.allPlays == 0 }
            if (allNotPlayed.isNotEmpty()) {
                allNotPlayed.shuffled().first()
            } else {
                node.childArray.shuffled().maxBy { (it.currentPlayerWins / it.allPlays) + (c * sqrt((2 * ln(node.allPlays.toDouble())) / it.allPlays)) }!!
            }
        }
    }

    private fun defaultPolicy(node: Node): Delta {
        return playout(node)
    }

    fun playout(node: Node): Delta {
//        println("Playout")
        with(node.gameState) {
            val playerOneCopy = playerOne.copy()
            val playerTwoCopy = playerTwo.copy()
            val game = Game(playerOneCopy, playerTwoCopy, round.copy(playerOneCopy, playerTwoCopy))

            game.startGameWithGreedy(RandomPlayer(), RandomPlayer())
            return if (game.winner == playerOneCopy) {
                node.playerOneWins++
                Delta(playerOneWins = 1)
            } else {
                node.playerTwoWins++
                Delta(playerTwoWins = 1)
            }
        }
    }

    data class Delta(
            val playerOneWins: Int = 0,
            val playerTwoWins: Int = 0
    )

    fun backPropagation(node: Node, delta: Delta) {
        node.parent?.let {
            it.playerOneWins += delta.playerOneWins
            it.playerTwoWins += delta.playerTwoWins
            backPropagation(it, delta)
        }
    }

    fun countChildren(node: Node, childAmount: Int): Int {
        var tmpAmount = childAmount
        node.childArray.forEach { tmpAmount += countChildren(it, childAmount) }
        return tmpAmount + 1
    }

    fun countLeafs(node: Node, leafs: Int): Int {
        var tmpLeafs = leafs
        node.childArray.forEach { tmpLeafs += countLeafs(it, leafs) }
        return tmpLeafs + if (node.childArray.size == 0) 1 else 0
    }

//    fun countLeaf__(node: Node, leaf: Leaf): Int {
//        var tmpLeaf = leaf
//        node.childArray.forEach { tmpLeaf += countLeaf__(it, leaf) }
//        return tmpLeaf + Leaf(minDepth = count = (if (node.childArray.size == 0) 1 else 0))
//    }

    fun countMaxDepthLeaf(node: Node, depth: Int): Int {
        var max = depth
        node.childArray.forEach {
            val tmp = countMaxDepthLeaf(it, depth + 1)
            if (tmp > max) max = tmp
        }
        return max
    }


    fun countMinDepthLeaf(node: Node, depth: Int): Int {
        val minList = mutableListOf<Int>()
        node.childArray.forEach {
            minList.add(countMinDepthLeaf(it, depth + 1))

        }
        return minList.min() ?: depth
    }

    data class Leaf(var minDepth: Int = 0, var maxDepth: Int = 0, var count: Long)

    fun printTree(node: Node) {
        node.printLine()
        node.childArray.forEach(this::printTree)
    }

    fun saveWinner(playerNumber: Int) {
        //println("Winner: $playerNumber")
        File(FILE_NAME + EXT).appendText("Winner: $playerNumber")
        File(FILE_NAME + EXT).appendText(System.lineSeparator())
        for (i in 1..5) {
          File(FILE_NAME + EXT).appendText(System.lineSeparator())
        }
    }
}