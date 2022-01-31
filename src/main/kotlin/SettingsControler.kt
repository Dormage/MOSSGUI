import it.zielke.moji.SocketClient
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Stage
import java.io.File
import java.net.URI
import java.net.URL
import kotlin.concurrent.thread
import kotlin.io.path.Path

/*
 * @created 28. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/
    
class SettingsControler (private val stage: Stage, private val main: Main){
    val dataManager: DataManager = main.dataManager
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
    }

    fun runMoss(){
        Thread {
            var socketClient = SocketClient()
            socketClient.userID = "632113431"
            socketClient.language = "java"
            socketClient.run()
            var currentProgress = 0
            dataManager.students.forEach {
                it.files.forEach{ file ->
                    socketClient.uploadFile(file)
                }
                Platform.runLater(Runnable {
                    uploadProgressBar.progress = currentProgress / dataManager.students.size.toDouble()
                })
                currentProgress++
            }
            socketClient.sendQuery()
            val resultUri: URL = socketClient.resultURL
            dataManager.addHistory(mossQuery(System.currentTimeMillis(), resultUri.toString()))
        }.start()
    }

}
