import java.util.*

data class Deck private constructor(
        var cards: List<Card>
) {
    private val deck: LinkedList<Card> = LinkedList(cards)

    companion object {
        fun newShuffled(): Deck {
            val packOfCards = packOfCards + packOfCards
            return Deck(packOfCards).apply {
                shuffle()
            }
        }
    }

    fun isEmpty() = deck.isEmpty()

    fun shuffle() {
        deck.shuffle()
    }

    fun getCard(): Card = deck.poll()

    fun getCard(card: Card): Int {
        val index = deck.indexOf(card)
        deck.removeAt(index)
        return index
    }

    fun copy(): Deck {
        var newDeck = LinkedList<Card>()
        deck.forEach { newDeck.add(it.copy()) }
        return Deck(newDeck)
    }
    fun getGroupped(): Map<String, List<Card>> {
        return deck.groupBy { it.name }
    }

    fun putCard(card: Card, index: Int) {
        deck.add(index, card)
    }
}


val packOfCards
    get() = listOf(
            Minion("Devilsaur", 5, 5, 5),
            Minion("Whelp", 1, 1, 1),
            Minion("Nether Imp", 2, 3, 2),
            Minion("River Crocolisk", 2, 2, 3),
            Minion("Shado-Pan Monk", 2, 2, 2),
            Minion("Twilight Flamecaller", 3, 2, 2, specialSkill = WearDown()),
            Spell("Assasinate", 5, "Destroy minion", negativeAction = DestroyMinion()),

            Minion("Hefty", 3, 4, 1, specialSkill = Snooze()),
            Spell("Helper", 5, "Add 2 hp to a minion", positiveAction = HelpMinion()),
            Spell("Snoozeder", 5, "Snooze minion for one round", negativeAction = SnoozeMinion())
    )