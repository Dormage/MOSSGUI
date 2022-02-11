import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import javafx.stage.Popup
import javafx.stage.Stage
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.tinyzip.TinyZip
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


/*
 * @created 28. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/

class MainControler(private val stage: Stage, private val main: Main){

    val dataManager: DataManager = main.dataManager
    @FXML
    private lateinit var loadProgress : ProgressBar
    @FXML
    private lateinit var mainPane : AnchorPane
    @FXML
    private lateinit var loadingTitle : Label
    @FXML
    private lateinit var loadingStatusLog : TextField
    @FXML
    private lateinit var popupPane: BorderPane
    @FXML
    private fun loadAssignments(event: ActionEvent) {
        println("load callback")
        DirectoryChooser().apply {
            title = "Select folder containing assignments"
            showDialog(stage)?.apply {
                val new :Parent = main.loadComponent("LoadingFiles.fxml", this@MainControler)
                mainPane.children.setAll(new)
                //creates temporary directory following required MOSS submission structure
                val tempDirectory: Path = Files.createTempDirectory("MOSS_")
                println("Created temporary folder at:  $tempDirectory")
                dataManager.url = tempDirectory.toString()
                Thread() {
                    var currentProgress = 0
                    val submissionDirs: Array<File> = absoluteFile.listFiles(DirectoryFileFilter.DIRECTORY as FileFilter)
                    submissionDirs.forEachIndexed { index, assignmentFolder ->
                        println("Processing student : ${assignmentFolder.name}$ ")
                        logLoadingProgress("Processing student : ${assignmentFolder.name}$ ...")
                        var error: String = ""
                        val compressed: Sequence<File> = assignmentFolder.walkTopDown().filter { assignmentFolder.toString().endsWith(".zip") }
                        compressed.forEach {file ->
                            try {
                                logLoadingProgress("Uncompressing assignment ...")
                                TinyZip.unzip(file.path.toString(), file.parent.toString())
                            } catch (e: Exception) {
                                error = e.toString()
                            }
                        }
                        //create temp submission folder
                        val tempStudentDir = Files.createDirectory(Paths.get(tempDirectory.toString() + File.separator + assignmentFolder.name.replace(" ","_") +File.separator))
                        val sourceCodeFiles = FileUtils.listFiles(File(assignmentFolder.toString()), arrayOf("java"), true)
                        sourceCodeFiles.forEach{ sourceFile ->
                            val destination = File(tempStudentDir.toString() + File.separator + sourceFile.name)
                            Files.copy(sourceFile.toPath(),destination.toPath(),StandardCopyOption.REPLACE_EXISTING)
                            println("Copy command from ${sourceFile.toPath()} to : $destination")
                            logLoadingProgress("Copying source files to $destination")
                        }
                        dataManager.addPreview(Preview(
                            index,
                            assignmentFolder.name,
                            tempStudentDir.toString(),
                            error,
                            sourceCodeFiles.toMutableList()
                        ))
                        Platform.runLater(Runnable {
                           loadProgress.progress = currentProgress.toDouble() / submissionDirs.size
                        })
                        currentProgress++
                    }
                    val preview :Parent = main.loadComponent("PreviewAssignments.fxml", SubmitControler(stage,main))
                    stage.scene.root = preview
                }.start()
            }
        }
    }
    private fun logLoadingProgress(progress:String){
        Platform.runLater(Runnable {
            loadingStatusLog.text = progress
        })
    }
    @FXML
    private fun loadHistory(){
    }
    @FXML private fun uploadAssignments(){
    }
    @FXML
    private fun openSettingsPopup(){
        val new : Parent = main.loadComponent("SettingsScreen.fxml", this@MainControler)
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
}



