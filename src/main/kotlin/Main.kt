
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application(){
    override fun start(primaryStage: Stage) {
        val mainControler = MainControler(primaryStage, this)
        loadComponent("LandingScreen.fxml", mainControler).apply {
            primaryStage.scene = Scene(this)
            primaryStage.show()
        }
    }

    fun loadComponent(path: String, controller: Any? = null): Parent {
        FXMLLoader(Main::class.java.getResource(path)).apply {
            setController(controller)
            return load()
        }
    }
    fun main() {
        System.setProperty("kotlinx.coroutines.scheduler", "off")
        Application.launch(Main::class.java)
    }
}
