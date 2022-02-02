import it.zielke.moji.SocketClient
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Stage
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL


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
    fun runMoss(){
        Thread {
            var socketClient = SocketClient()
            socketClient.userID = "632113431"
            socketClient.language = "java"
            socketClient.run()
            var currentProgress = 0

            FileUtils.listFiles(File(dataManager.url), arrayOf("java"), true).forEach{
                socketClient.uploadFile(it)
                println("Uploaded $it")
                Platform.runLater(Runnable {
                    uploadProgressBar.progress = currentProgress / dataManager.students.size.toDouble()
                })
                currentProgress++
            }
            socketClient.sendQuery()
            val resultUri: URL = socketClient.resultURL
            println(resultUri)
            dataManager.addHistory(mossQuery(System.currentTimeMillis(), resultUri.toString()))
        }.start()
    }
}
