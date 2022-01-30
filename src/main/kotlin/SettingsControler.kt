import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Stage

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
    fun initialize(){
        sId.setCellValueFactory ( PropertyValueFactory<Student,Int>("Id"))
        sName.setCellValueFactory ( PropertyValueFactory<Student,String>("Name"))
        sUrl.setCellValueFactory ( PropertyValueFactory<Student,String>("Url"))
        sFiles.setCellValueFactory ( PropertyValueFactory<Student,Int>("Files"))
        sError.setCellValueFactory ( PropertyValueFactory<Student,String>("Error"))
        submissions.items.addAll(dataManager.students)
    }

}