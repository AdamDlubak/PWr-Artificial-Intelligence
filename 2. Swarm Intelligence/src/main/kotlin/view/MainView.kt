package view

import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.layout.StackPane
import logic.FurnitureRenderEvent
import logic.FurnitureRenderEventC
import logic.RoomController
import tornadofx.*

class MainView : View() {

    private val controller: RoomController by inject()

    override val root: StackPane = prepareSpace {
        buttonOnClick("Start") {
            controller.compute()
        }

        subscribe<FurnitureRenderEvent> {
            this@prepareSpace += it.furniture
        }


        subscribe<FurnitureRenderEventC> {
            this@prepareSpace += it.furniture
        }

    }

    private fun prepareSpace(paddingValue: Double = 0.0, onGroup: Group.() -> Unit = {}): StackPane {
        return stackpane {
            padding = Insets(paddingValue)
            group {
                this += RoomUI()
                onGroup()
            }
        }
    }
}

fun EventTarget.buttonOnClick(text: String = "", onClick: () -> Unit = {}) = button(text) {
    action { onClick() }
}