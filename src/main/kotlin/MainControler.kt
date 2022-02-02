import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ProgressBar
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.DirectoryChooser
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
    private fun loadAssignments(event: ActionEvent) {
        DirectoryChooser().apply {
            title = "Select folder containing assignments"
            showDialog(stage)?.apply {
                val fxmlLoader = FXMLLoader(MainControler::class.java.getResource("LoadingFiles.fxml"))
                fxmlLoader.setController(this)
                val yourNewView = fxmlLoader.load<BorderPane>()
                mainPane.children.setAll(yourNewView)
                //creates temporary directory following required MOSS submission structure
                val tempDirectory: Path = Files.createTempDirectory("MOSS_")
                println("Created temporary folder at:  $tempDirectory")
                dataManager.url = absoluteFile.toString()
                val thread = Thread() {
                    Thread.sleep(1000)
                    var currentProgress = 0
                    val submissionDirs: Array<File> = absoluteFile.listFiles(DirectoryFileFilter.DIRECTORY as FileFilter)
                    submissionDirs.forEach {
                        println("Processing student : ${it.name}$ ")
                        var error: String = ""
                        val compressed: Sequence<File> = it.walkTopDown().filter { it.toString().endsWith(".zip") }
                        compressed.forEachIndexed { index, file ->
                            try {
                                println("Uncompressing ...")
                                TinyZip.unzip(file.path.toString(), file.parent.toString())
                            } catch (e: Exception) {
                                error = e.toString()
                            }
                        }
                        //create temp submission folder
                        val tempStudentDir = Files.createDirectory(Paths.get(tempDirectory.toString() + File.separator + it.name+File.separator))
                        FileUtils.listFiles(File(it.toString()), arrayOf("java"), true).forEach{ sourceFile ->
                            val destination: File = File(tempStudentDir.toString() + File.separator + sourceFile.name)
                            Files.copy(sourceFile.toPath(),destination.toPath(),StandardCopyOption.REPLACE_EXISTING)
                            println("Copy command from ${sourceFile.toPath().toString()} to : $destination")
                        }

                        Platform.runLater(Runnable {
                           loadProgress.progress = currentProgress.toDouble() / submissionDirs.size
                        })
                        currentProgress++
                    }
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