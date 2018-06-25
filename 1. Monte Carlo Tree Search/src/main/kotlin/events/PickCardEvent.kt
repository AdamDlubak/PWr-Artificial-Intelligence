package events

import Event
import Card
import Player

class PickCardEvent(
        val player: Player,
        val card: Card
) : Event {
    var index: Int = 0

    override fun execute() {
        index = player.pickCard(card)
//        print()
    }

    override fun rollBack() {
        player.unPicCard(card, index)
    }

    override fun print() {
        println("Pick card ${card.name}")
    }
}