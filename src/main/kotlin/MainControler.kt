import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.tinyzip.TinyZip
import java.io.File
import java.nio.file.Files
import kotlin.io.path.isDirectory

/*
 * @created 28. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/
    
class MainControler(private val stage: Stage, private val main: Main){

    val dataManager: DataManager = main.dataManager

    @FXML
    private lateinit var loadAssignments: Button
    @FXML
    private lateinit var history: Button
    @FXML
    private lateinit var loadProgress : ProgressBar

    @FXML
    private fun loadAssignments(event: ActionEvent){
        DirectoryChooser().apply {
            title = "Select folder containing assignments"
            showDialog(stage)?.apply {
                val thread = Thread() {
                    var currentProgress = 0
                    val total = Files.walk(absoluteFile.toPath(),1).filter { it.isDirectory() }.count() -1;
                    Files.walk(absoluteFile.toPath(),1).filter { it.isDirectory() }.forEach {
                        val url: String = it.toString()
                        val name: String = url.replace(absoluteFile.toString(),"")
                        var error: String = ""
                        val compressed: Sequence<File> = File(url).walkTopDown().filter { it.toString().endsWith(".zip") }
                        compressed.forEachIndexed { index, file ->
                            try {
                                TinyZip.unzip(file.path.toString(), file.parent.toString())
                            } catch (e: Exception) {
                                error = e.toString()
                            }
                        }
                        val sourceFiles: Sequence<File> = File(url).walkTopDown().filter { it.isFile && it.toString().endsWith(".java") }
                        dataManager.addStudent(Student(currentProgress,name,url,error,sourceFiles.toMutableList()))

                        Platform.runLater(Runnable {
                            loadProgress.progress = currentProgress.toDouble() / total
                        })
                        currentProgress++
                    }
                    Platform.runLater(Runnable {
                        val settingsControler = SettingsControler(stage, main)
                        main.loadComponent("SettingsScreen.fxml", settingsControler).apply {
                            stage.scene = Scene(this)
                        }
                    })
                }.start()
            }
        }
    }

    @FXML
    private fun loadHistory(){

    }
}

data class Student(
    val id : Int,
    val name: String,
    val url: String,
    val error: String,
    val files: MutableList<File>
    )