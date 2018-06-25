class RandomPlayer() : GreedyPlayer {
    override fun chooseEvent(events: List<Event>) = events.shuffled().first()

    override fun toString(): String {
        return "RandomPlayer"
    }
}