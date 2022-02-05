import it.zielke.moji.SocketClient
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Main : Application() {
    val dataManager: DataManager = DataManager()

    override fun start(primaryStage: Stage) {
        val socketClient = SocketClient()
        socketClient.userID = "632113431"
        socketClient.language = "java"
        socketClient.run()
        try {
            Files.walk(Paths.get("/Users/mihael/Downloads/MOSS_283712831731989469")).use { paths ->
                paths.sorted().forEach { path: Path ->
                    if (path.toString().endsWith(".java")) {
                        try {
                            println("Uploading: $path")
                            val newFile = path.toFile()
                            val newData = newFile.readText().replace("[^\\x00-\\x7F]+".toRegex(), "")
                            newFile.writeText(newData)
                            socketClient.uploadFile(newFile)
                            Thread.sleep(100)
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
        print(results)


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

