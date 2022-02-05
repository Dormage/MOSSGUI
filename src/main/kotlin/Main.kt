
import it.zielke.moji.SocketClient
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Main : Application(){
    val dataManager: DataManager = DataManager()

    override fun start(primaryStage: Stage) {
        val socketClient = SocketClient()
        socketClient.userID = "632113431"
        socketClient.language = "java"
        socketClient.run()
        try {
            Files.walk(Paths.get("/tmp/MOSS_5231636663982958914")).use { paths ->
                paths.forEach { path: Path ->
                    if (path.toString().endsWith(".java")) {
                        try {
                            println("Uploading: $path")
                            socketClient.uploadFile(path.toFile())
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println("Socket status ${socketClient.socket.isConnected}  Stage: ${socketClient.currentStage}")
        socketClient.sendQuery();
        val results = socketClient.resultURL
        println(results)


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

