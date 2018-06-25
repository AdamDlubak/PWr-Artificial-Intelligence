sealed class Card {
    abstract val name: String
    abstract var cost: Int

    abstract fun showCard()
    abstract fun copy() : Card
}
data class Minion(
        override val name: String,
        override var cost: Int,
        var attack: Int,
        var hp: Int,
        var isSnoozed: Int = 0,
        var playedInRound: Boolean = false,
        val specialSkill: SpecialSkill? = null
) : Card() {
    override fun showCard() {
        if(hp >= 0) print("|  $name C: $cost A: $attack HP: $hp  |")
        else print("|  $name C: $cost A: $attack HP: 0  |")
    }
    override fun copy(): Minion = Minion(name, cost, attack, hp, isSnoozed, playedInRound, specialSkill)

}

data class Spell(
        override val name: String,
        override var cost: Int,
        val description: String,
        val positiveAction: SpellAction? = null,
        val negativeAction: SpellAction? = null
) : Card() {
    override fun showCard() {
        print("|  $name C: $cost D: $description  |")
    }
    override fun copy() : Spell = Spell(name, cost, description, positiveAction, negativeAction)
}