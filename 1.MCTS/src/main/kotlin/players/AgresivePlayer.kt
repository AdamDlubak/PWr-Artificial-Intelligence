class AgresivePlayer : GreedyPlayer {
    override fun chooseEvent(events: List<Event>): Event {
        val heroEventsList = mutableListOf<AttackHeroEvent>()
        val attackMinionsEventsList = mutableListOf<AttackMinionEvent>()
        val eventsList = mutableListOf<Event>()

        events.forEach {
            if (it is AttackHeroEvent) {
                heroEventsList.add(it)
            } else if (it is AttackMinionEvent && heroEventsList.isEmpty()) {
                attackMinionsEventsList.add(it)
            } else if (attackMinionsEventsList.isEmpty()) {
                eventsList.add(it)
            }
        }
        return heroEventsList.maxBy { it.attack }
                ?: attackMinionsEventsList.maxBy { it.controlFactor }
                ?: eventsList.shuffled().first()
    }

    override fun toString(): String {
        return "AgresivePlayer"
    }
}