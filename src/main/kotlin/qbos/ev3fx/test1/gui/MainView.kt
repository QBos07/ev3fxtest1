package qbos.ev3fx.test1.gui

import javafx.scene.control.ListView
import javafx.scene.layout.Pane
import javafx.scene.input.KeyEvent
import tornadofx.*

class MainView: View("ev3fxtest1 Main GUI") {
    override val root: Pane by fxml()
    val ev3listProperty = ""
    val ev3list: ListView<String> by fxid("lev3list")

    @Suppress("unused")
    fun bev3addEonKeyTyped(e: KeyEvent) {
        if(e.character == "\n") {

        }
    }
}