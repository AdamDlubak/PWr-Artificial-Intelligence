class EndGameEvent(
        private val winner: Player,
        private val uiManager: UIManager
) : Event {
    override fun execute() {
        uiManager.finishGame(winner.hero.name)
        //System.exit(1)
//        print()
    }

    override fun rollBack() {
        // no-op
    }

    override fun print() {
        // no-op
    }
}