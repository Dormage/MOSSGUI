import javafx.event.ActionEvent
import javafx.fxml.FXML

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import org.tinyzip.TinyZip
import java.io.File
import java.nio.file.Path

/*
 * @created 28. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/
    
class MainControler(private val stage: Stage, private val main: Main){
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
                println(this.absoluteFile)
                val settingsControler = SettingsControler(stage,main)

                val compressed : Sequence<File> = File(this.absoluteFile.toPath().toString()).walkTopDown().filter { it.toString().endsWith(".zip") }
                val assignments : Sequence<File>  = File(this.absoluteFile.toPath().toString()).walkTopDown().filter { it.toString().endsWith(".java") }
                val total = compressed.count() + assignments.count();

                compressed.forEachIndexed { index, file -> try {
                    TinyZip.unzip(file.path.toString(), file.parent.toString())
                    }catch (e: Exception){ }
                    loadProgress.progress = ((index / total).toDouble())
                }

                assignments.forEachIndexed {  index, file ->
                    println(file)
                    loadProgress.progress = ((index / total).toDouble())
                }

                /*
                main.loadComponent("SettingsScreen.fxml", settingsControler).apply {
                    stage.scene = Scene(this)
                }*/
            }
        }
    }

    @FXML
    private fun loadHistory(){

    }
}