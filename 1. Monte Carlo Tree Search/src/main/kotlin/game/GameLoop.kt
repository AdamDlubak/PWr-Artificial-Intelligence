
interface GameLoop {
    fun waitForEvent(): Event
    fun dispatchEvent(event: Event)
    fun renderView()
}