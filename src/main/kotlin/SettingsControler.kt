import it.zielke.moji.SocketClient
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/*
 * @created 28. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/
    
class SettingsControler (private val stage: Stage, private val main: Main){
    val dataManager: DataManager = main.dataManager
    @FXML
    private lateinit var mainPane : AnchorPane
    @FXML
    private lateinit var loadingStatusLog : TextField
    @FXML
    private lateinit var loadProgress : ProgressBar
    @FXML
    private lateinit var submissions: TableView<Student>
    @FXML
    private lateinit var sId: TableColumn<Student,Int>
    @FXML
    private lateinit var sName: TableColumn<Student,String>
    @FXML
    private lateinit var sUrl: TableColumn<Student,String>
    @FXML
    private lateinit var sFiles: TableColumn<Student,Int>
    @FXML
    private lateinit var sError: TableColumn<Student,String>
    @FXML
    private lateinit var uploadButton : Button
    @FXML
    private lateinit var mossKey : Label
    @FXML
    private lateinit var uploadProgressBar : ProgressBar

    @FXML
    fun initialize(){
        sId.setCellValueFactory ( PropertyValueFactory<Student,Int>("Id"))
        sName.setCellValueFactory ( PropertyValueFactory<Student,String>("Name"))
        sUrl.setCellValueFactory ( PropertyValueFactory<Student,String>("Url"))
        sFiles.setCellValueFactory ( PropertyValueFactory<Student,Int>("Files"))
        sError.setCellValueFactory ( PropertyValueFactory<Student,String>("Error"))
        submissions.items.addAll(dataManager.students)
        submissions.setRowFactory { tv ->
            object : TableRow<Student?>() {
                override fun updateItem(item: Student?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item != null) {
                        style =
                            if (item.name.isEmpty()) "-fx-background-color: #FFD2D2;"
                            else if (item.files.size == 0) "-fx-background-color: #baffba;"
                            else if (item.error.isNotEmpty()) "-fx-background-color: #FEEFB3;"
                            else " \"-fx-background-color: #BDE5F8;\""
                    }
                }
            }
        }
    }

    @FXML
    private fun uploadAssignments (event: ActionEvent){
        val new : Parent = main.loadComponent("LoadingFiles.fxml", this@SettingsControler)
        mainPane.children.setAll(new)
        Thread() {
            val socketClient = SocketClient()
            socketClient.userID = "632113431"
            socketClient.language = "java"
            socketClient.run()
            //upload .java files
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
        }.start()
    }

    private fun logLoadingProgress(progress:String){
        Platform.runLater(Runnable {
            loadingStatusLog.text = progress
        })
    }
}
