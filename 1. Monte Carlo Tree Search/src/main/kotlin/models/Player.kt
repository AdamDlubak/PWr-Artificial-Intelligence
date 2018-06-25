data class Player(
        val hero: Hero,
        val deck: Deck,
        var magicPoint: Int = 0,
        val handCards: MutableList<Card> = mutableListOf(),
        val tableCards: MutableList<Card> = mutableListOf(),
        val usedCards: MutableList<Card> = mutableListOf()
) {

    fun pickCards(quantity: Int) {
        for (i in 0 until quantity) {
            handCards.add(deck.getCard())
        }
    }

    fun putOnTable(minion: Minion) {
        if (handCards.remove(minion)) {
            tableCards.add(minion)
            minion.playedInRound = true
            magicPoint -= minion.cost
        }
    }

    fun returnToHand(minion: Minion) {
        if (tableCards.remove(minion)) {
            handCards.add(minion)
            minion.playedInRound = false
            magicPoint += minion.cost
        }
    }

    fun takeDown(card: Card) {
        if (tableCards.remove(card)) usedCards.add(card)
        if (handCards.remove(card)) usedCards.add(card)
    }

    fun resurrect(card: Minion) {
        if (usedCards.remove(card)) tableCards.add(card)
    }
    fun resurrectSpell(card: Spell) {
        if (usedCards.remove(card)) handCards.add(card)
    }
    fun copy(): Player = Player(
            hero = hero.copy(),
            deck =  deck.copy(),
            magicPoint = magicPoint,
            handCards = copyCards(handCards),
            tableCards = copyCards(tableCards),
            usedCards = copyCards(usedCards)
    )
    private fun copyCards(cards: MutableList<Card>): MutableList<Card> {
        val newList = mutableListOf<Card>()
        cards.forEach { newList.add(it.copy()) }
        return newList

    }
    fun pickCard(card: Card): Int {
        val index = deck.getCard(card)
        handCards.add(card)
        return index
    }

    fun unPicCard(card: Card, index: Int) {
        if (handCards.remove(card)) deck.putCard(card, index)
    }
}