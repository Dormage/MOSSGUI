import it.zielke.moji.SocketClient
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Popup
import javafx.stage.Stage
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*


/*
 * @created 28. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/
    
class SubmitControler (private val stage: Stage, private val main: Main){
    val dataManager: DataManager = main.dataManager
    @FXML
    private lateinit var mainPane : AnchorPane
    @FXML
    private lateinit var loadingStatusLog : TextField
    @FXML
    private lateinit var loadProgress : ProgressBar
    @FXML
    private lateinit var loadingTitle : Label
    @FXML
    private lateinit var submissions: TableView<Preview>
    @FXML
    private lateinit var sId: TableColumn<Preview,Int>
    @FXML
    private lateinit var sName: TableColumn<Preview,String>
    @FXML
    private lateinit var sUrl: TableColumn<Preview,String>
    @FXML
    private lateinit var sFiles: TableColumn<Preview,Int>
    @FXML
    private lateinit var sError: TableColumn<Preview,String>
    @FXML
    private lateinit var popupPane: BorderPane
    @FXML
    fun initialize(){
        sId.setCellValueFactory ( PropertyValueFactory<Preview,Int>("Id"))
        sName.setCellValueFactory ( PropertyValueFactory<Preview,String>("Name"))
        sUrl.setCellValueFactory ( PropertyValueFactory<Preview,String>("Url"))
        sFiles.setCellValueFactory ( PropertyValueFactory<Preview,Int>("Files"))
        sError.setCellValueFactory ( PropertyValueFactory<Preview,String>("Error"))
        submissions.items.addAll(dataManager.previews)
        submissions.setRowFactory { tv ->
            object : TableRow<Preview?>() {
                override fun updateItem(item: Preview?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item != null) {
                        style =
                            if (item.name.isEmpty()) "-fx-background-color: #FFD2D2;"
                            else if (item.files.size == 0) "-fx-background-color: #e8892a;"
                            else if (item.error.isNotEmpty()) "-fx-background-color: #FEEFB3;"
                            else " \"-fx-background-color: #BDE5F8;\""
                    }
                }
            }
        }
    }
    @FXML
    private fun openSettingsPopup(){
        val new : Parent = main.loadComponent("SettingsScreen.fxml", this@SubmitControler)
        val popup: Popup = Popup()
        popup.scene.fill= Color.web("FFFFFF")
        popup.width = 400.0
        popup.height = 300.0
        popup.isAutoFix = true;
        popup.isAutoHide = true;
        popup.isHideOnEscape = true;
        popupPane.border = Border(BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))
        popup.content.addAll(new)
        popup.show(stage)
        val close: Button = new.lookup("#settingsDone") as Button
        val event: EventHandler<ActionEvent?> = EventHandler<ActionEvent?> { popup.hide() }
        close.onAction = event
    }

    @FXML
    private fun uploadAssignments (){
        val new : Parent = main.loadComponent("LoadingFiles.fxml", this@SubmitControler)
        mainPane.children.setAll(new)
        Platform.runLater(Runnable { loadingTitle.text = "Uploading files" })
        Thread() {
            val socketClient = SocketClient()
            socketClient.userID = "632113431"
            socketClient.language = "java"
            socketClient.run()
            var currentProgress = 0
            //upload .java files
            val files = FileUtils.listFiles(File(dataManager.url), arrayOf("java"), true)
            files.forEach {
                logLoadingProgress("Uploading $it ...")
                println("Loading file $it")
                val newData = it.readText().replace("[^\\x00-\\x7F]+".toRegex(), "")
                it.writeText(newData)
                socketClient.uploadFile(it)
                Platform.runLater(Runnable {
                    loadProgress.progress = currentProgress.toDouble() / files.size
                })
                currentProgress++
            }
            println("Socket status ${socketClient.socket.isConnected}  Stage: ${socketClient.currentStage}")
            Platform.runLater(Runnable { logLoadingProgress("Waiting for results...") })
            Platform.runLater(Runnable { loadingTitle.text = "Waiting for server" })
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Platform.runLater(Runnable {
                        loadProgress.progress = (loadProgress.progress + 0.1) %1
                    })
                    if(socketClient.currentStage == it.zielke.moji.Stage.AWAITING_END){
                        cancel()
                    }
                }
            },0,100)
            socketClient.sendQuery();
            val results = socketClient.resultURL
            println(results)
            dataManager.parseMossResult(results)
        }.start()
    }

    private fun logLoadingProgress(progress: String){
        Platform.runLater(Runnable {
            loadingStatusLog.text = progress
        })
    }

}
