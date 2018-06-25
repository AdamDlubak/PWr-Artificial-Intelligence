import java.util.*

class ControlPlayer() : GreedyPlayer {
    override fun chooseEvent(events: List<Event>): Event {

        val attackMinionsEventsList = mutableListOf<AttackMinionEvent>()
        val playMinionEventsList = mutableListOf<PlayMinionEvent>()
        val heroEventsList = mutableListOf<AttackHeroEvent>()
        val eventsList = mutableListOf<Event>()

        events.forEach {
            if (it is AttackMinionEvent) {
                attackMinionsEventsList.add(it)
            } else if (it is PlayMinionEvent && attackMinionsEventsList.isEmpty()) {
                playMinionEventsList.add(it)
            } else if (it is AttackHeroEvent && playMinionEventsList.isEmpty()) {
                heroEventsList.add(it)
            } else if (heroEventsList.isEmpty()) {
                eventsList.add(it)
            }
            attackMinionsEventsList.sortBy { it.controlFactor }
            playMinionEventsList.sortBy { it.factor }
            heroEventsList.sortBy { it.attack }
            eventsList.shuffle()

        }
        return attackMinionsEventsList.maxBy { it.controlFactor }
                ?: playMinionEventsList.maxBy { it.factor }
                ?: heroEventsList.maxBy { it.attack }
                ?: eventsList.shuffled().first()
    }

    override fun toString(): String {
        return "ControlPlayer"
    }
}
