class EndTurnEvent(
        private val roundManger: RoundManger
) : Event {
    override fun execute() {
        roundManger.endTurn()
//        print()
    }
    fun executeWithoutCardPicking() {
        roundManger.endTurn()
    }

    override fun rollBack() {
        // no-op
    }

    override fun print() {
        println("End your turn")
    }
}