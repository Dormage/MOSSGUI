import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URL

class Main : Application() {
    val dataManager: DataManager = DataManager()

    override fun start(primaryStage: Stage) {
        val mainControler = MainControler(primaryStage, this)
        loadComponent("Main.fxml", mainControler).apply {
            primaryStage.scene = Scene(this)
            primaryStage.show()
        }
        dataManager.parseMossResult(URL("http://moss.stanford.edu/results/2/9840761405389"))
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

